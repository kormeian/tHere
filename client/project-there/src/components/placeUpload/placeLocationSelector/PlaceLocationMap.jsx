import React, { useState, useEffect, useRef, useMemo } from 'react';
import styled from 'styled-components';
import { Map } from 'react-kakao-maps-sdk';
import PlaceSearchResultList from './PlaceSearchResultList';
import PlaceEventMarkerContainer from './PlaceEventMarkerContainer';
import PlaceSelectButton from './PlaceSelectButton';
import PlaceCancleButton from './PlaceCancleButton';
import { findPointLocation } from './placeLocationSelectorActions';

const StyledMapHolder = styled.div`
  position: fixed;
  top : 10vh;
  left : 20vw;
  width: 60vw;
  height: 80vh;
  background-color: white;
  z-index: 12;
  display: flex;
`;

export default function PlaceLocationMap(
  { controlPointPlace, controlSelectPlace, controlMapOpen, controlPointAddress }) {
  const [placeHover, setPlaceHover] = useState('');
  const [pointAddress, setPointAddress] = controlPointAddress;
  const [pointPlaces, setPointPlaces] = controlPointPlace;
  const [placeSelected, setPlaceSelected] = controlSelectPlace;
  const mapRef = useRef();
  const [mapCreate, setMapCreate] = useState(false);

  // 장소 주소들이 변경되었을 때, bounds가 변경되면 bounds를 변경해줌
  const bounds = useMemo(() => {
    const newBounds = new window.kakao.maps.LatLngBounds();
    pointPlaces?.forEach((place) => {
      newBounds.extend(new window.kakao.maps.LatLng(place[2], place[3]));
    });
    return newBounds;
  }, [pointPlaces]);

  // pointAddress가 변경되었을 때 해당되는 장소의 정보를 검색해서 pointPlaces에 넣어줌
  useEffect(() => {
    if (pointAddress?.length !== 0) {
      const results = findPointLocation(pointAddress);
      results.then((res) => {
        if (res?.length !== 0) {
          setPointPlaces(res);
        }
      });
    }
  }, [pointAddress]);

  // map이 생성되었을 때, map의 bound를 결정해줌
  useEffect(() => {
    const map = mapRef.current;
    if (map && pointPlaces.length !== 0) {
      map.setBounds(bounds);
    }
  }, [bounds, mapCreate, pointPlaces]);

  return (
    <StyledMapHolder>
      <PlaceSearchResultList
        setPoint={{ pointPlaces, setPointPlaces }}
        setHover={{ placeHover, setPlaceHover }}
        setSelected={controlSelectPlace}
        setPointAddress={setPointAddress}
      />
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
        onCreate={() => { setMapCreate(true); }}
      >
        {pointPlaces?.map((point) => {
          let hoverd = false;
          let selected = false;
          if (placeHover === point[0]) {
            hoverd = true;
          }
          if (placeSelected === point[0]) {
            selected = true;
          }
          return (
            <PlaceEventMarkerContainer
              key={`${point[0]}-${point[2]}-${point[3]}`}
              position={{
                lat: point[2],
                lng: point[3],
              }}
              content={point[0]}
              hoverd={hoverd}
              selected={selected}
            />
          );
        })}
      </Map>
      <PlaceSelectButton
        selected={placeSelected !== ''}
        setMapOpen={controlMapOpen}
      />
      <PlaceCancleButton
        selected={placeSelected !== ''}
        setPlaceSelected={setPlaceSelected}
      />
    </StyledMapHolder>
  );
}
