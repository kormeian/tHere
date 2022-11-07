import React, { useState } from 'react';
import styled from 'styled-components';
import PlaceSearchResultElement from './PlaceSearchResultElement';

const ResultWrapper = styled.div`
  width : 40%;
  max-width: 40%;
  min-width: 30%;
  display: flex;
  flex-direction: column;
`;
const PlaceSearchArea = styled.div`
  width: 100%;
  height: 15%;
  display: flex;
  align-items: center;
  position:relative;
  background-color: var(--color-green100);
`;
const SearchInput = styled.input`
  height: 30%;
  width: 90%;
  margin-left: 10px;
  border-radius: 20px;
  padding-left: 10px;
`;
const SearchButton = styled.button`
  background-color : var(--color-green200);
  height: 30%;
  width: 50px;
  border: 1px solid black;
  position:absolute;
  right: 5%;
  display: flex;
  justify-content: center;
  align-items: center;
`;
const ResultList = styled.ol`
  list-style: none;
  width: 100%;
  height: 85%;
  display: flex;
  flex-direction: column;
  align-items: center;
  overflow : auto;
`;
const NoResultWrapper = styled.div`
  margin-top: 10%;
`;

export default function PlaceSearchResultList({ setPoint, setHover,
  setSelected, setPointAddress }) {
  const { pointPlaces, setPointPlaces } = setPoint;
  const { placeHover, setPlaceHover } = setHover;
  const [placeSelected, setPlaceSelected] = setSelected;
  const [searchOpen, setSearchOpen] = useState(false);

  const handleClick = (e) => {
    let { target } = e;
    if (target.tagName === 'OL') {
      return;
    }
    if (target.tagName === 'DIV') {
      target = target.closest('li');
    }
    target = target.querySelector('div');
    if (target.dataset.key !== placeSelected) {
      setPlaceSelected(target.dataset.key);
    }
  };

  const handleSearchButtonClick = (e) => {
    let target = e.target.closest('div');
    target = target.querySelector('input');
    if (target.value !== '') {
      setPointAddress([target.value]);
    }
  };

  return (
    <ResultWrapper>
      <PlaceSearchArea>
        <SearchInput
          placeholder="주소를 검색해주세요!"
          onFocus={() => { setSearchOpen(true); }}
          onBlur={() => { setSearchOpen(false); }}
        />
        <SearchButton
          onClick={handleSearchButtonClick}
        >
          검색
        </SearchButton>
      </PlaceSearchArea>
      <ResultList
        onMouseLeave={() => { setPlaceHover(''); }}
        onClick={handleClick}
      >
        {pointPlaces.length === 0 && (
          <NoResultWrapper>
            검색 결과가 없습니다.
            <br />
            <br />
            다시 검색해주세요
          </NoResultWrapper>
        )}
        {pointPlaces?.map((place) => {
          let selected = false;
          if (place[0] === placeSelected) {
            selected = true;
          }
          return (
            <PlaceSearchResultElement
              placeInfo={place}
              key={`${place[0]}-${place[1]}`}
              setHover={setHover}
              selected={selected}
            />
          );
        })}
      </ResultList>
    </ResultWrapper>
  );
}
