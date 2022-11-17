import React, { useEffect, useState, useCallback } from 'react';
import styled from 'styled-components';
import { useInView } from 'react-intersection-observer';
import DotLoader from 'react-spinners/DotLoader';
import { useJourneyListActions } from '../../contexts/JourneyListContext';
import JourneyList from '../main/JourneyList';
import ProfileBox from '../common/ProfileBox';

const Wrapper = styled.div`
  padding-top: 150px;
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const Observer = styled.div`
  margin-top: 60px;
`;

function MyJourney({ memberId }) {
  const { initDatas, loadMyJourneyItems } = useJourneyListActions();

  // 무한 스크롤
  const [page, setPage] = useState(0);
  const [ref, inView] = useInView();
  const [isLoading, setIsLoading] = useState(false);
  const [observer, setObserver] = useState(true);

  // 서버에서 아이템을 가지고 오는 함수
  const getItems = useCallback(async () => {
    setIsLoading(true);
    const isLast = await loadMyJourneyItems(page);
    setIsLoading(false);

    // 마지막 페이지일 때 observer 제거
    if (isLast) setObserver(false);
    else setObserver(true);
  }, [page]);

  // 옵저버가 viewport 안에 있고, 로딩 중이 아닐 때 페이지 up
  useEffect(() => {
    if (inView && !isLoading) {
      setPage((prev) => prev + 1);
    }
  }, [inView]);

  // getItems가 바뀔 때마다 함수 실행
  useEffect(() => {
    getItems();
  }, [getItems]);

  // 언마운트 될 때 여정 목록 초기화
  useEffect(() => initDatas, []);

  return (
    <Wrapper>
      <ProfileBox />
      <JourneyList page="myJourney" />
      {observer && (
        <Observer ref={ref}>
          <DotLoader color="#51A863" size="30px" />
        </Observer>
      )}
    </Wrapper>
  );
}

export default MyJourney;
