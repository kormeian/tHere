import React, { useContext, useEffect, useState, useRef } from 'react';
import styled from 'styled-components';
import { VerticalTimeline } from 'react-vertical-timeline-component';
import 'react-vertical-timeline-component/style.min.css';
import PlaceTimeLineElement from './PlaceTimeLineElement';
import PlaceTimeLineTimeIndicator from './PlaceTimeLineTimeIndicator';
import Places from '../../contexts/Places';

const PlacesDetailsHolder = styled.div`
  width: 100%;
  height: 60%;
  .vertical-timeline {
    padding-left: 5vw;
    padding-top: 0px;
  }
`;
const StyledVerticalTimeline = styled(VerticalTimeline)`
  ::before {
    width: 2px !important;
    margin-left: 5vw;
  }
`;

export default function PlacesDetails({ focusedPlace, hover }) {
  const targetPlacesData = useContext(Places);
  const [targetPlaceList, setTargetPlaceList] = useState([
    { elapsedTime: 1, date: '' },
  ]);
  const holderRef = useRef();
  const [hoverPlace, setHoverPlace] = hover;

  const handleMouseOver = (e) => {
    const targetElement = e.currentTarget.className.split(' ')[2].slice(24);
    if (hoverPlace !== targetElement) {
      setHoverPlace(targetElement);
    }
  };

  useEffect(() => {
    if (!targetPlacesData) {
      return;
    }
    if (targetPlacesData.length !== 0) {
      let preDate = targetPlacesData[0].placeTime.slice(0, 10);
      let elapsedTime = 1;
      const targetList = [];
      targetList.push({ date: preDate, elapsedTime });
      targetPlacesData?.forEach((target) => {
        const targetDate = target.placeTime.slice(0, 10);
        if (targetDate !== preDate) {
          preDate = targetDate;
          elapsedTime += 1;
          targetList.push({ date: preDate, elapsedTime });
        }
        targetList.push(target);
      });
      setTargetPlaceList(targetList);
    } else {
      setTargetPlaceList([{ elapsedTime: 1, date: '' }]);
    }
  }, [targetPlacesData?.length]);

  useEffect(() => {
    if (holderRef) {
      const $timeLineElement = holderRef.current.querySelectorAll(
        '.vertical-timeline-element',
      );
      $timeLineElement?.forEach((element) => {
        const classNameList = element.className.split(' ');
        element.addEventListener('mouseover', handleMouseOver);
      });
    }
  }, [targetPlaceList]);

  return (
    <PlacesDetailsHolder ref={holderRef}>
      <StyledVerticalTimeline layout="1-column-left" lineColor="#51A863">
        {targetPlaceList?.map((target) => {
          if (target.elapsedTime) {
            return (
              <PlaceTimeLineTimeIndicator
                elapsedTime={target.elapsedTime}
                date={target.date}
                key={target.elapsedTime}
              />
            );
          }
          return (
            <PlaceTimeLineElement
              target={target}
              key={`${target.placeId}-${target.placeName}`}
              focusedPlace={focusedPlace}
            />
          );
        })}
      </StyledVerticalTimeline>
    </PlacesDetailsHolder>
  );
}
