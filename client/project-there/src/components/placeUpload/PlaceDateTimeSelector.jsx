import React, { useContext, useState, useEffect } from 'react';
import styled from 'styled-components';
import DateTimePicker from 'react-datetime-picker';
import { usePlaceInfoValue, usePlaceInfoActions } from '../../contexts/PlaceInfoContext';

const StyledDateTimeHolder = styled.div`
  width: 100%;
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
  }
  .timeSelectButton{
    background-color: var(--color-green100);
    color: white;
    position: absolute;
    bottom: 10px;
    right: 10px;
  }
  .datetimePickerHolder{
    width: 100%;
    height: 20vh;
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
    border: 0.5px solid var(--color-green200);
    background-color: var(--color-gray100);
    border-radius: 20px;
  }
`;
const StyledDateTimePicker = styled(DateTimePicker)`
  button{
    color: black;
  }
  .react-calendar__month-view__days__day--weekend{
    color:red;
  }
`;

export default function PlaceDateTimeSelector() {
  const placeInfo = usePlaceInfoValue();
  const { updateData } = usePlaceInfoActions();
  const [imageTakenTime, setImageTakenTime] = useState('');
  const [timerOpen, setTimerOpen] = useState(false);
  const [timeSelected, setTimeSelected] = useState('');

  const setPlaceTakenTime = () => {
    updateData('placeTime', imageTakenTime);
  };

  const findDateTime = (time) => {
    const year = time.getFullYear();
    const month = time.getMonth() + 1;
    const date = time.getDate();
    const hour = time.getHours();
    const minute = time.getMinutes();

    const stringTime = `${year}년 ${month}월 ${date}일 ${hour}시 ${minute}분`;
    return stringTime;
  };

  useEffect(() => {
    if (imageTakenTime !== '') {
      const time = findDateTime(imageTakenTime);
      setTimeSelected(time);
    }
  }, [imageTakenTime]);

  useEffect(() => {
    console.log(placeInfo.placeTime);
    if (placeInfo.placeTime !== '') {
      setImageTakenTime(placeInfo.placeTime);
    }
  }, [placeInfo.placeTime]);

  const handleSelectButtonClick = () => {
    setTimerOpen(false);
    setPlaceTakenTime();
  };

  return (
    <StyledDateTimeHolder>
      {!timerOpen && (
        <div>
          시간
          <button
            type="button"
            onClick={() => { setTimerOpen(true); }}
            className="displayButton"
          >
            {timeSelected === '' ? '선택' : timeSelected}
          </button>
        </div>
      )}
      {timerOpen && (
        <div
          className="datetimePickerHolder"
        >
          <StyledDateTimePicker
            value={imageTakenTime}
            onChange={setImageTakenTime}
          />
          <button
            onClick={handleSelectButtonClick}
            type="button"
            className="timeSelectButton"
          >
            선택
          </button>
        </div>
      )}
    </StyledDateTimeHolder>
  );
}
