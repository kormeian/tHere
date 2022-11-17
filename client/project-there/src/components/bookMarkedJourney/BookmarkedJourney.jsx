import React, { useEffect, useState, useCallback } from 'react';
import styled from 'styled-components';
import { useInView } from 'react-intersection-observer';
import DotLoader from 'react-spinners/DotLoader';
import { useJourneyListActions } from '../../contexts/JourneyListContext';
import JourneyList from '../main/JourneyList';
import { useAuthValue } from '../../contexts/AuthContext';
import { ReactComponent as BookmarkSVG } from '../../assets/icons/bookmark.svg';

const Wrapper = styled.div`
  padding-top: 190px;
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const Observer = styled.div`
  margin-top: 60px;
`;

const TitleBox = styled.div`
  margin-bottom: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 984px;
  padding: 22px 0;
  border-bottom: 0.5px solid var(--color-gray300);
  color: var(--color-green200);

  & span:first-child {
    font-size: var(--font-regular);
    font-weight: var(--weight-semi-bold);
    color: var(--color-green300);
    margin-right: 4px;
  }
`;

const BookmarkIcon = styled(BookmarkSVG)`
  width: 60px;
  height: 60px;
  margin-bottom: 18px;
`;

function BookmarkedJourney({ memberId }) {
  const { initDatas, loadBookmarkedItems } = useJourneyListActions();
  const { nickName } = useAuthValue();

  // 무한 스크롤
  const [page, setPage] = useState(0);
  const [ref, inView] = useInView();
  const [isLoading, setIsLoading] = useState(false);
  const [observer, setObserver] = useState(true);

  // 서버에서 아이템을 가지고 오는 함수
  const getItems = useCallback(async () => {
    setIsLoading(true);
    const isLast = await loadBookmarkedItems(page);
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
      <TitleBox>
        <span>{nickName}</span>
        <span>님의 북마크</span>
      </TitleBox>
      <JourneyList page="bookmark" />
      {observer && (
        <Observer ref={ref}>
          <DotLoader color="#51A863" size="30px" />
        </Observer>
      )}
    </Wrapper>
  );
}

export default BookmarkedJourney;
