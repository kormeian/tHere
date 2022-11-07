import React from 'react';
import styled from 'styled-components';
import { useJourneyDetailValue } from '../../../contexts/journeyDetail';
import colors from '../../../lib/constants/colors';

const { gray300 } = colors;

const Container = styled.div`
  position: relative;

  &::after {
    content: '';
    display: block;
    margin-top: 36px;
    width: 100%;
    border: 0.5px solid ${gray300};
  }
`;

const Title = styled.h3`
  margin: 20px 0;
  margin-bottom: 40px;
  font-size: 42px;
  font-weight: 100;
`;

function TitleArea() {
  const { title, region, nickName } = useJourneyDetailValue();

  return (
    <Container>
      <button type="button">{region}</button>
      <Title>{title}</Title>
      <div>
        <span>이미지 </span>
        <span>{nickName}</span>
      </div>
    </Container>
  );
}

export default TitleArea;
