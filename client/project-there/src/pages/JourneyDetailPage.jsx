import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { useParams, useNavigate } from 'react-router-dom';
import JourneyDetails from '../components/journeyDetail/JourneyDetails';
import JourneyMap from '../components/journeyDetail/JourneyMap';
import { placeData } from '../datas/placeData';
import Places from '../contexts/Places';
import CategoryItemButton from '../components/journeyDetail/CategoryItemButton';
import { baseAxios, authAxios } from '../lib/utills/customAxios';
import { useAuthValue, useAuthActions } from '../contexts/auth';
import { getAccessToken } from '../lib/utills/controlAccessToken';

const JourneyHolder = styled.div`
  width: 100%;
  height: 100%;
  position: fixed;
  border: 1px solid black;
  display: flex;
  align-items: center;
  min-width: 1200px;
`;
const ButtonHolder = styled.button`
  position: absolute;
  z-index: 12;
  right: 30px;
  bottom: 30px;
  background-color: var(--color-green100);
  color: white;
  height: 39px;
`;
const CategoryDisplay = styled.div`
  background-color: var(--color-green200);
  color: var(--color-gray100);
  position:absolute;
  z-index: 12;
  top: 55px;
  font-size: var(--font-regular);
  width: 20%;
  border-radius: 0 0 30px 0;
  border-color: var(--color-gray300);
  display: flex;
  flex-direction: column;
  padding: 20px 20px;
`;
const CategoryList = styled.div`
  width: 100%;
  height: 140px;
  border-top: 1px solid var(--color-green100);
  margin-top: 10px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  padding-top: 6px;
`;
const categoryOptions = [
  '자연',
  '숙박',
  '음식점',
  '레저',
  '테마파크',
  '쇼핑',
  '유적지',
  '박물관',
  '공연',
  '전시회',
  '캠핑',
  '키즈',
  '기타',
];
function PlaceInfoProvider({ children, value }) {
  return <Places.Provider value={value}>{children}</Places.Provider>;
}
export default function JourneyDetailPage() {
  const [totalPlacesData, setTotalPlacesData] = useState([]);
  const [targetPlacesData, setTargetPlacesData] = useState([]);
  const [focusedPlace, setFocusedPlace] = useState('');
  const [hoverPlace, setHoverPlace] = useState('');
  const [categoryOpen, setCategoryOpen] = useState(false);
  const [categorySelected, setCategorySelected] = useState([]);
  const [nickName, setNickName] = useState('');
  const params = useParams();
  const navigate = useNavigate();
  const userInfo = useAuthValue();
  const { authenticateUser } = useAuthActions();

  const handleButtonClick = () => {
    navigate(`/placeupload/${params.journeyId}`);
  };

  const handleCategoryButtonClick = () => {
    setCategoryOpen((res) => !res);
  };

  useEffect(() => {
    // access token을 가져와 userInfo를 갱신함
    const accessToken = getAccessToken();
    authenticateUser(accessToken);

    // 서버로부터 데이터 전송 받음
    const url = `place/list?journeyId=${params.journeyId}`;
    authAxios.get(url)
      .then(({ data }) => {
        console.log(data);
        setTotalPlacesData(data);
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);

  useEffect(() => {
    if (userInfo !== '' && nickName !== '') {
      console.log(nickName);
      console.log(userInfo);
    }
  }, [userInfo, nickName]);

  // // 자체 데이터로 테스트 할 때 사용함
  // useEffect(() => {
  //   setTotalPlacesData(placeData);
  // }, []);

  // 카테고리 변할 때 파악해줌
  useEffect(() => {
    if (categorySelected.length === 0) {
      if (totalPlacesData.length !== 0) {
        setTargetPlacesData(totalPlacesData);
      }
    } else {
      const newTarget = totalPlacesData?.filter((place) => {
        if (categorySelected.includes(place.placeCategory)) {
          return true;
        }
        return false;
      });
      setTargetPlacesData(() => newTarget);
    }
  }, [categorySelected.length]);

  // 데이터가 들어왔을 때, 표시해줌
  useEffect(() => {
    if (totalPlacesData?.length !== 0) {
      setTargetPlacesData(totalPlacesData);
    }
  }, [totalPlacesData]);

  return (
    <JourneyHolder>
      <PlaceInfoProvider value={targetPlacesData}>
        <CategoryDisplay
          onClick={handleCategoryButtonClick}
        >
          Category
          {categoryOpen && (
            <CategoryList>
              {categoryOptions?.map((category) => (
                <CategoryItemButton
                  key={category}
                  category={category}
                  setSelected={[categorySelected, setCategorySelected]}
                />
              ))}
            </CategoryList>
          )}
        </CategoryDisplay>
        <JourneyMap
          setFocus={setFocusedPlace}
          hoverPlace={hoverPlace}
        />
        <JourneyDetails
          focusedPlace={focusedPlace}
          hover={[hoverPlace, setHoverPlace]}
          journeyId={params.journeyId}
          controlNickName={[nickName, setNickName]}
        />
      </PlaceInfoProvider>
      <ButtonHolder type="button" onClick={handleButtonClick}>
        장소 추가하기
      </ButtonHolder>
    </JourneyHolder>
  );
}
