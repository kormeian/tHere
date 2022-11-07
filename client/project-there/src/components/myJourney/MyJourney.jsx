import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { useJourneyListActions } from '../../contexts/journeyList';
import JourneyList from '../main/JourneyList';

const Wrapper = styled.div`
  padding-top: 60px;
  display: flex;
  flex-direction: column;
  align-items: center;
`;

function MyJourney({ memberId }) {
  const { loadMyJourneyItems } = useJourneyListActions();
  const [page, setPage] = useState(0);

  useEffect(() => {
    loadMyJourneyItems(memberId, page);
  }, []);

  return (
    <Wrapper>
      <JourneyList />
    </Wrapper>
  );
}

export default MyJourney;
