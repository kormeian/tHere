//package onde.there.journey.service;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//import javax.persistence.EntityManager;
//import onde.there.domain.Journey;
//import onde.there.domain.JourneyTheme;
//import onde.there.domain.Member;
//import onde.there.domain.type.JourneyThemeType;
//import onde.there.domain.type.RegionType;
//import onde.there.dto.journy.JourneyDto;
//import onde.there.dto.journy.JourneyDto.CreateRequest;
//import onde.there.dto.journy.JourneyDto.CreateResponse;
//import onde.there.journey.exception.JourneyErrorCode;
//import onde.there.journey.exception.JourneyException;
//import onde.there.journey.repository.JourneyRepository;
//import onde.there.journey.repository.JourneyThemeRepository;
//import onde.there.member.repository.MemberRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.transaction.annotation.Transactional;
//
//@WebAppConfiguration
//@SpringBootTest
//@Transactional
//public class JourneyServiceTest2 {
//
//	@Autowired
//	private JourneyRepository journeyRepository;
//
//	@Autowired
//	private JourneyThemeRepository journeyThemeRepository;
//
//	@Autowired
//	private JourneyService journeyService;
//
//	@Autowired
//	private MemberRepository memberRepository;
//
//	@Autowired
//	private EntityManager entityManager;
//
//
//	@Test
//	void createJourneySuccess() throws IOException {
//		//given
//		Member member = new Member("tHereId", "tHereEmail", "tHerePassword",
//			"온데", "testNickname");
//		memberRepository.save(member);
//
//		String fileName = "1";
//		String contentType = "png";
//		String filePath = "src/main/resources/testImages/1.png";
//		FileInputStream fileInputStream = new FileInputStream(
//			new File(filePath));
//		MockMultipartFile mockMultipartFile = new MockMultipartFile(fileName,
//			fileName + "." + contentType, contentType, fileInputStream);
//
//		//when
//		CreateResponse journeyDto = journeyService.createJourney(
//			CreateRequest.builder()
//				.title("TitleTest")
//				.startDate(LocalDate.parse("2022-10-16"))
//				.endDate(LocalDate.parse("2022-10-17"))
//				.disclosure("public")
//				.journeyThemes(Arrays.asList("힐링", "식도락"))
//				.introductionText("테스트 소개 글")
//				.numberOfPeople(7)
//				.region("서울")
//				.build(),
//			mockMultipartFile,
//			"tHereId"
//		);
//
//		//then
//		assertEquals("testNickname", journeyDto.getNickName());
//		assertEquals("TitleTest", journeyDto.getTitle());
//		assertEquals(LocalDate.parse("2022-10-16"),
//			journeyDto.getStartDate());
//		assertEquals(LocalDate.parse("2022-10-17"),
//			journeyDto.getEndDate());
//		assertEquals("힐링", journeyDto.getJourneyThemes().get(0));
//		assertEquals("식도락", journeyDto.getJourneyThemes().get(1));
//		assertEquals("서울", journeyDto.getRegion());
//
//	}
//
//	@Test
//	@DisplayName("종료 날짜가 시작 날짜보다 과거 - 여정 생성 실패")
//	void createJourney_DateError() throws IOException {
//
//		Member member = new Member("tHereId", "tHereEmail", "tHerePassword",
//			"온데", "testNickname");
//		memberRepository.save(member);
//
//		FileInputStream fis = new FileInputStream(
//			"src/main/resources/testImages/" + "1.png");
//		MockMultipartFile testThumbnail = new MockMultipartFile("1", "1.png",
//			"png", fis);
//
//		JourneyException exception = assertThrows(JourneyException.class,
//			() -> journeyService.createJourney(
//				CreateRequest.builder()
//					.title("TitleTest")
//					.startDate(LocalDate.parse("2022-10-16"))
//					.endDate(LocalDate.parse("2022-10-15"))
//					.disclosure("public")
//					.journeyThemes(Arrays.asList("힐링", "식도락"))
//					.introductionText("테스트 소개 글")
//					.numberOfPeople(7)
//					.region("서울")
//					.build(),
//				testThumbnail,
//				"tHereId"));
//
//		assertEquals(JourneyErrorCode.DATE_ERROR, exception.getErrorCode());
//	}
//
//	@Test
//	@DisplayName("일치하는 region 없을 때 - 여정 생성 실패")
//	void createJourney_NO_AREA_MATCHES() throws IOException {
//
//		Member member = new Member("tHereId", "tHereEmail", "tHerePassword",
//			"온데", "testNickname");
//		memberRepository.save(member);
//
//		String fileName = "1";
//		String contentType = "png";
//		String filePath = "src/main/resources/testImages/1.png";
//		FileInputStream fileInputStream = new FileInputStream(
//			new File(filePath));
//		MockMultipartFile mockMultipartFile = new MockMultipartFile(fileName,
//			fileName + "." + contentType, contentType, fileInputStream);
//
//		JourneyException exception = assertThrows(JourneyException.class,
//			() -> journeyService.createJourney(
//				JourneyDto.CreateRequest.builder()
//					.title("TitleTest")
//					.startDate(LocalDate.parse("2022-10-16"))
//					.endDate(LocalDate.parse("2022-10-17"))
//					.disclosure("public")
//					.journeyThemes(Arrays.asList("힐링", "식도락"))
//					.introductionText("테스트 소개 글")
//					.numberOfPeople(7)
//					.region("없는지역")
//					.build(),
//				mockMultipartFile,
//				"tHereId"));
//
//		assertEquals(JourneyErrorCode.NO_REGION_MATCHES,
//			exception.getErrorCode());
//	}
//
//	@Test
//	void MyJourneyListSuccess() {
//		//given
//		Member member = new Member("tHereId", "tHereEmail", "tHerePassword",
//			"온데", "testNickname");
//		memberRepository.save(member);
//
//		List<Journey> journeyList = Arrays.asList(
//			Journey.builder()
//				.member(member)
//				.title("TitleTest")
//				.startDate(LocalDate.parse("2022-10-16"))
//				.endDate(LocalDate.parse("2022-10-17"))
//				.disclosure("public")
//				.introductionText("테스트 소개 글")
//				.numberOfPeople(7)
//				.region(RegionType.SEOUL)
//				.build(),
//			Journey.builder()
//				.member(member)
//				.title("TitleTest2")
//				.startDate(LocalDate.parse("2022-10-16"))
//				.endDate(LocalDate.parse("2022-10-17"))
//				.disclosure("public")
//				.introductionText("테스트 소개 글")
//				.numberOfPeople(7)
//				.region(RegionType.GYEONGGI)
//				.build()
//		);
//
//		List<JourneyTheme> journeyTheme = Arrays.asList(
//			JourneyTheme.builder()
//				.journey(journeyList.get(0))
//				.journeyThemeName(JourneyThemeType.HEALING)
//				.build(),
//			JourneyTheme.builder()
//				.journey(journeyList.get(1))
//				.journeyThemeName(JourneyThemeType.RESTAURANT)
//				.build()
//		);
//
////		for (Journey journey : journeyList) {
////			entityManager.persist(journey);
////		}
////
////		for (JourneyTheme theme : journeyTheme) {
////			entityManager.persist(theme);
////		}
//
//		journeyRepository.saveAll(journeyList);
//		journeyThemeRepository.saveAll(journeyTheme);
//
//		PageRequest pageable = PageRequest.of(0, 2);
//
//		//when
//		Page<Journey> result = journeyRepository
//			.journeyListByMemberId("tHereId", pageable);
//
//		//then
//		assertThat(result.getSize()).isEqualTo(2);
//		assertThat(result.getContent().get(0).getTitle()).isEqualTo(
//			"TitleTest");
//		assertThat(result.getContent().get(1).getTitle()).isEqualTo(
//			"TitleTest2");
//		assertThat(result.getContent().get(0).getRegion()).isEqualTo(
//			RegionType.SEOUL);
//		assertThat(result.getContent().get(1).getRegion()).isEqualTo(
//			RegionType.GYEONGGI);
//
//
//	}
//}
