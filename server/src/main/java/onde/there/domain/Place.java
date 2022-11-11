package onde.there.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
import lombok.ToString;
import onde.there.domain.type.PlaceCategoryType;
import org.hibernate.annotations.BatchSize;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@ToString
public class Place {

	@Id
	@Column(name = "place_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Double latitude;
	private Double longitude;

	private String title;

	@Column(length = 1010)
	private String text;

	private String addressName;

	private String region1;

	private String region2;

	private String region3;

	private String region4;
	private String placeName;

	private LocalDateTime placeTime;

	@Enumerated(EnumType.STRING)
	private PlaceCategoryType placeCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "journey_id")
	private Journey journey;

	private long placeHeartCount;

	@BatchSize(size = 11)
	@OneToMany(mappedBy = "place")
	private List<PlaceImage> placeImages = new ArrayList<>();
}
