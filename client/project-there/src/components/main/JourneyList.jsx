import React from 'react';
import styled from 'styled-components';
import JourneyCard from './JourneyCard';
import { useJourneyListValue } from '../../contexts/journeyList';
import JourneyDetailProvider from '../../contexts/journeyDetail';

const Wrapper = styled.div`
  width: 100vw;
  display: flex;
  justify-content: center;
`;

const JourneyListBox = styled.div`
  display: grid;
  place-items: center;
  grid-template-columns: repeat(3, 300px);
  grid-gap: 64px 42px;
`;

function JourneyList() {
  const [journeyList] = useJourneyListValue();

  return (
    <JourneyDetailProvider>
      <Wrapper>
        <JourneyListBox>
          {journeyList.map((item) => (
            <JourneyCard
              key={item.journeyId}
              memberId={item.nickName}
              title={item.title}
              journeyId={item.journeyId}
              region={item.region}
              journeyThumbnailUrl={item.journeyThumbnailUrl}
            />
          ))}
        </JourneyListBox>
      </Wrapper>
    </JourneyDetailProvider>
  );
}

export default JourneyList;
