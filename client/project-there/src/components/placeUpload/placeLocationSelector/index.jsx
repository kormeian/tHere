import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { usePlaceInfoValue, usePlaceInfoActions } from '../../../contexts/PlaceInfoContext';
import PlaceLocationMap from './PlaceLocationMap';
import { makePlaceInfoLocation, coord2AddressSearch } from './placeLocationSelectorActions';

const LocationHolder = styled.div`
  width: 80%;
  height: 10%;
  display:flex;
  align-items: center;
  color: var(--color-gray500);
  margin-left: 2%;
  font-size: var(--font-small);
  .displayButton{
    margin-left: 60px;
    color: var(--color-green100);
    border: 0.5px solid var(--color-green100);
    padding: 0.5em 1em;
  };
  .modalBackground{
    position : fixed;
    top :0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0,0,0,0.7);
    z-index:10;
  }
`;

export default function PlaceLocationSelector() {
  const [mapOpen, setMapOpen] = useState(false);
  const [pointAddress, setPointAddress] = useState([]);
  const [pointPlaces, setPointPlaces] = useState([]);
  const [placeSelected, setPlaceSelected] = useState('');
  const [selectedInfo, setSelectedInfo] = useState([]);
  const placeInfo = usePlaceInfoValue();
  const { updateMultiData } = usePlaceInfoActions();

  // 업데이트로 값이 갱신되었을 때, placeSelected와 pointPlace 변경시켜줌
  useEffect(() => {
    if (placeInfo.placeName !== '') {
      setPlaceSelected(placeInfo.placeName);
      const { placeName, addressName, latitude, longitude } = placeInfo;
      setPointPlaces([[placeName, addressName, latitude, longitude]]);
    }
  }, [placeInfo.placeName]);

  // 이미지가 업로드 되었을 때, 좌표를 주소로 변환시키고
  // 이 값을 pointAddress에 저장해줌
  useEffect(() => {
    if (placeInfo?.imageTakenLocations?.length !== 0) {
      console.log('hi');
      const points = [...placeInfo.imageTakenLocations];
      Promise
        .all(points?.map((point) => coord2AddressSearch(point.lng, point.lat)))
        .then((results) => {
          const uniqueResult = Array.from(new Set(results));
          if (uniqueResult?.length !== 0) {
            setPointAddress(uniqueResult);
          }
        });
    }
  }, [placeInfo.imageTakenLocations]);

  // 선택된 장소를 selectedInfo에 갱신해줌
  useEffect(() => {
    pointPlaces?.forEach((place) => {
      if (place[0] === placeSelected) {
        setSelectedInfo(place);
      }
    });
    if (placeSelected === '') {
      setSelectedInfo([]);
    }
  }, [placeSelected]);

  // 선택된 장소의 정보를 context에 넣어줌
  useEffect(() => {
    if (selectedInfo.length !== 0 && !mapOpen) {
      const [keyList, valueList] = makePlaceInfoLocation(selectedInfo);
      updateMultiData(keyList, valueList);
    }
  }, [mapOpen]);

  return (
    <LocationHolder>
      {!mapOpen && (
        <div>
          위치
          <button
            type="button"
            onClick={() => { setMapOpen(true); }}
            className="displayButton"
          >
            {placeSelected === '' ? '선택' : placeSelected}
          </button>
        </div>
      )}
      {mapOpen && <div className="modalBackground" />}
      {mapOpen && (
        <PlaceLocationMap
          controlPointAddress={[pointAddress, setPointAddress]}
          controlPointPlace={[pointPlaces, setPointPlaces]}
          controlSelectPlace={[placeSelected, setPlaceSelected]}
          controlMapOpen={setMapOpen}
        />
      )}
    </LocationHolder>
  );
}
