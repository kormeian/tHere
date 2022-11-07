import React, { useContext, useEffect, useState, useMemo, useRef } from 'react';
import styled from 'styled-components';
import { Map, MarkerClusterer } from 'react-kakao-maps-sdk';
import Places from '../../contexts/Places';
import CustomMapMarker from './CustomMapMarker';

const JourneyMapHolder = styled.div`
  width: 40%;
  height: 100%;
  background-color: grey;
  display: flex;
  justify-content: center;
  align-items: center;
`;

export default function JourneyMap({ setFocus, hoverPlace }) {
  const [targetPlaces, setTargetPlaces] = useState([]);
  const targetPlacesData = useContext(Places);
  const mapRef = useRef();
  useEffect(() => {
    if (targetPlacesData) {
      setTargetPlaces(targetPlacesData);
    }
  }, [targetPlacesData]);

  // 장소 주소들이 변경되었을 때, bounds가 변경되면 bounds를 변경해줌
  const bounds = useMemo(() => {
    const newBounds = new window.kakao.maps.LatLngBounds();
    targetPlaces?.forEach((place) => {
      newBounds.extend(new window.kakao.maps.LatLng(place.latitude, place.longitude));
    });
    return newBounds;
  }, [targetPlaces]);

  // map이 생성되었을 때, map의 bound를 결정해줌
  useEffect(() => {
    const map = mapRef.current;
    if (map && targetPlaces.length !== 0) {
      map.setBounds(bounds);
    }
  }, [bounds, targetPlaces]);

  return (
    <JourneyMapHolder>
      <Map
        center={{
          // 지도의 중심좌표
          lat: 33.450701,
          lng: 126.570667,
        }}
        style={{
          // 지도의 크기
          width: '100%',
          height: '100%',
        }}
        level={3}
        ref={mapRef}
      >
        <MarkerClusterer
          averageCenter
          minLevel={6}
          minClusterSize={5}
        >
          {targetPlaces?.map((place) => (
            <CustomMapMarker
              position={{ lat: place.latitude, lng: place.longitude }}
              thumbnail={place.imageUrls[0]}
              key={`${place.placeName}-${place.placeId}`}
              setFocus={setFocus}
              placeId={place.placeId}
              hoverPlace={hoverPlace}
              placeCategory={place.placeCategory}
            />
          ))}
        </MarkerClusterer>

      </Map>
    </JourneyMapHolder>
  );
}
