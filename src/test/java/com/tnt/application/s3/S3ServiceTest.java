package com.tnt.application.s3;

import static com.tnt.common.constant.ImageConstant.TRAINEE_DEFAULT_IMAGE;
import static com.tnt.domain.member.MemberType.TRAINEE;
import static com.tnt.domain.member.MemberType.TRAINER;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.tnt.common.error.exception.ImageException;
import com.tnt.infrastructure.s3.S3Adapter;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

	@InjectMocks
	private S3Service s3Service;

	@Mock
	private S3Adapter s3Adapter;

	private byte[] createDummyImageData() throws IOException {
		BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", baos);

		return baos.toByteArray();
	}

	@Test
	@DisplayName("트레이너 프로필 이미지 업로드 성공")
	void upload_trainer_profile_image_success() throws Exception {
		// given
		MockMultipartFile image = new MockMultipartFile("image", "test.jpg", IMAGE_JPEG_VALUE, createDummyImageData());
		String expectedUrl = "https://bucket.s3.amazonaws.com/trainer/profile/123.jpg";

		given(s3Adapter.uploadFile(any(byte[].class), anyString(), anyString())).willReturn(expectedUrl);

		// when
		String result = s3Service.uploadProfileImage(image, TRAINER);

		// then
		assertThat(result).isEqualTo(expectedUrl);
	}

	@Test
	@DisplayName("트레이니 프로필 이미지가 null일 경우 기본 이미지 반환 성공")
	void return_trainee_default_image_success() {
		// when
		String result = s3Service.uploadProfileImage(null, TRAINEE);

		// then
		assertThat(result).isEqualTo(TRAINEE_DEFAULT_IMAGE);
	}

	@Test
	@DisplayName("지원하지 않는 이미지 형식으로 업로드 실패")
	void upload_profile_image_unsupported_format_error() throws IOException {
		// given
		MockMultipartFile image = new MockMultipartFile("image", "test.gif", "image/gif", createDummyImageData());

		// when & then
		assertThrows(ImageException.class, () -> s3Service.uploadProfileImage(image, TRAINER));
	}

	@Test
	@DisplayName("orientation이 3일 때 이미지 180도 회전 성공")
	void rotate_image_orientation_3_success() {
		// given
		BufferedImage originalImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = originalImage.createGraphics();

		// 위쪽은 흰색, 아래쪽은 검은색으로 채워서 회전 확인이 가능하도록 함
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, 100, 50);
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 50, 100, 50);
		graphics.dispose();

		// when
		BufferedImage rotatedImage = ReflectionTestUtils.invokeMethod(s3Service, "rotateImageByOrientation",
			originalImage, 3);

		// then
		// 회전 후에는 위쪽이 검은색, 아래쪽이 흰색이어야 함
		assertThat(requireNonNull(rotatedImage).getRGB(50, 25)).isEqualTo(Color.BLACK.getRGB()); // 원래 아래쪽 색
		assertThat(requireNonNull(rotatedImage).getRGB(50, 75)).isEqualTo(Color.WHITE.getRGB()); // 원래 위쪽 색
	}
}
