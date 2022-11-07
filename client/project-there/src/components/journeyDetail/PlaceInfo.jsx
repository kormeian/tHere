import React, { useRef } from 'react';
import styled from 'styled-components';
import ImageDisplayCarousel from './ImageDisplayCarousel';
import PlaceDetailInfo from './PlaceDetailInfo';

const PlaceInfoHolder = styled.div`
  width: 100%;
  height: 100%;
  border : 0.5px solid #2B5643;
  display: flex;
  align-items: center;
  padding: 10px;
  border-radius: 10px;
  margin-top: 10px;
`;

// 특정 장소의 정보를 보여주는 컴포넌트
export default function PlaceInfo({ target, edit }) {
  const placeHolder = useRef();
  return (
    <PlaceInfoHolder
      ref={placeHolder}
    >
      <ImageDisplayCarousel images={target.imageUrls} containerRef={placeHolder} />
      <PlaceDetailInfo target={target} edit={edit} />
    </PlaceInfoHolder>
  );
}
