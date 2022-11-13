package onde.there.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import onde.there.domain.type.RegionType;
import org.hibernate.annotations.BatchSize;

@Entity(name = "journey")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Journey {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "journey_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(name = "title")
	private String title;

	@Column(name = "start_day")
	private LocalDate startDate;

	@Column(name = "end_day")
	private LocalDate endDate;

	@Column(name = "journey_thumbnail_url")
	private String journeyThumbnailUrl;

	@Column(name = "disclosure")
	private String disclosure;

	@Column(name = "introduction_Text")
	private String introductionText;

	@Column(name = "number_of_people")
	private int numberOfPeople;

	@Enumerated(EnumType.STRING)
	@Column(name = "region")
	private RegionType region;

	@OneToMany(mappedBy = "journey")
	@BatchSize(size = 10)
	private List<JourneyTheme> journeyThemes = new ArrayList<>();

	@Column(name = "deleted")
	private boolean deleted;
}
