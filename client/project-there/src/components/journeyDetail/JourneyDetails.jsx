import React, { useEffect, useRef } from 'react';
import styled from 'styled-components';
import PlacesDetails from './PlacesDetails';
import JourneyInfo from './journeyInfo/JourneyInfo';
import JourneyDetailProvider from '../../contexts/journeyDetail';
import NewJourneyProvider from '../../contexts/newJourney';

const JourneyDetailsHolder = styled.div`
  width: 66.66vw;
  height: 100%;
  display: flex;
  align-items: center;
  flex-direction: column;
  overflow-y: auto;
  border-radius: 20px;
  margin-top: 120px;
`;

export default function JourneyDetails({
  focusedPlace,
  hover,
  journeyId,
  controlNickName,
}) {
  const holderRef = useRef();

  useEffect(() => {
    if (holderRef) {
      const $targetTimeLineElement = holderRef.current.querySelector(
        `.verticalTimeLineElement-${focusedPlace}`,
      );
      if ($targetTimeLineElement) {
        $targetTimeLineElement.scrollIntoView({
          behavior: 'smooth',
          block: 'center',
          inline: 'nearest',
        });
      }
    }
  }, [focusedPlace]);

  return (
    <JourneyDetailsHolder ref={holderRef}>
      <NewJourneyProvider>
        <JourneyDetailProvider>
          <JourneyInfo
            journeyId={journeyId}
            controlNickName={controlNickName}
          />
        </JourneyDetailProvider>
      </NewJourneyProvider>
      <PlacesDetails focusedPlace={focusedPlace} hover={hover} />
    </JourneyDetailsHolder>
  );
}
