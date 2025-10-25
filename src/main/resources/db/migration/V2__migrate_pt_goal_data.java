package db.migration;

import static com.tnt.trainee.domain.PtGoal.BODY_PROFILE;
import static com.tnt.trainee.domain.PtGoal.FLEXIBILITY_ENHANCE;
import static com.tnt.trainee.domain.PtGoal.HEALTH_MANAGE;
import static com.tnt.trainee.domain.PtGoal.POSTURE_CORRECTION;
import static com.tnt.trainee.domain.PtGoal.STRENGTH_ENHANCE;
import static com.tnt.trainee.domain.PtGoal.WEIGHT_LOSS;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnt.trainee.domain.PtGoal;

public class V2__migrate_pt_goal_data extends BaseJavaMigration {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void migrate(Context context) throws Exception {
		// 1. pt_goal 테이블에서 모든 데이터 조회
		Map<Long, List<PtGoal>> traineeGoalsMap = new HashMap<>();

		String selectSql = "SELECT trainee_id, content FROM pt_goal WHERE deleted_at IS NULL";
		try (PreparedStatement selectStmt = context.getConnection().prepareStatement(selectSql)) {
			ResultSet rs = selectStmt.executeQuery();
			while (rs.next()) {
				Long traineeId = rs.getLong("trainee_id");
				String content = rs.getString("content");

				PtGoal ptGoal = mapContentToPtGoal(content);
				if (ptGoal != null) {
					traineeGoalsMap.computeIfAbsent(traineeId, k -> new ArrayList<>()).add(ptGoal);
				}
			}
		}

		// 2. trainee 테이블 업데이트
		String updateSql = "UPDATE trainee SET pt_goals = ? WHERE id = ?";
		try (PreparedStatement updateStmt = context.getConnection().prepareStatement(updateSql)) {
			for (Map.Entry<Long, List<PtGoal>> entry : traineeGoalsMap.entrySet()) {
				Long traineeId = entry.getKey();
				List<PtGoal> ptGoals = entry.getValue();

				String jsonGoals = objectMapper.writeValueAsString(ptGoals);
				updateStmt.setString(1, jsonGoals);
				updateStmt.setLong(2, traineeId);
				updateStmt.executeUpdate();
			}
		}
	}

	private PtGoal mapContentToPtGoal(String content) {
		return switch (content.trim()) {
			case "근력 향상" -> STRENGTH_ENHANCE;
			case "체중 감량" -> WEIGHT_LOSS;
			case "건강 관리" -> HEALTH_MANAGE;
			case "자세 교정" -> POSTURE_CORRECTION;
			case "바디프로필" -> BODY_PROFILE;
			case "유연성향상" -> FLEXIBILITY_ENHANCE;
			default -> null;
		};
	}
}
