package onde.there.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "journey_bookmark")
public class JourneyBookmark {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "journey_bookmark_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "journey_id")
	private Journey journey;

	@Builder
	public JourneyBookmark(Member member, Journey journey) {
		this.member = member;
		this.journey = journey;
	}

}
