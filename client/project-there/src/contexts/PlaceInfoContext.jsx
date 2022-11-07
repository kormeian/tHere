import React, { createContext, useContext, useMemo, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAxios, baseAxios } from '../lib/utills/customAxios';

const PlaceInfoValueContext = createContext();
const PlaceInfoActionsContext = createContext();

const InitialPlaceInfo = {
  latitude: '',
  longitude: '',
  title: '',
  text: '',
  placeCategory: '',
  addressName: '',
  region1: '',
  region2: '',
  region3: '',
  region4: '',
  placeTime: new Date(),
  placeName: '',
  images: [],
  imageTakenLocations: [],
  journeyId: 1,
};

const WarningMatching = {
  latitude: '위치',
  longitude: '위치',
  title: '제목',
  text: '내용',
  placeCategory: '카테고리',
  addressName: '위치',
  region1: '위치',
  region2: '위치',
  region3: '위치',
  region4: '위치',
  placeTime: new Date(),
  placeName: '위치',
  images: '이미지 파일',
};

function PlaceInfoProvider({ children }) {
  const [placeInfo, setPlaceInfo] = useState(InitialPlaceInfo);
  const navigation = useNavigate();

  const makeFormData = useCallback((journeyId, placeId = '') => {
    const formData = new FormData();
    const uploadTargetData = { ...placeInfo };
    let submitPossible = true;

    delete uploadTargetData.imageTakenLocations;
    const placeKeys = Object.keys(uploadTargetData);
    placeKeys.forEach((key) => {
      const placeValue = uploadTargetData[key];
      if (submitPossible) {
        if (placeValue === '' || placeValue?.length === 0) {
          window.alert(`${WarningMatching[key]}를 입력해주세요!`);
          submitPossible = false;
        }
        if (key === 'images') {
          placeValue.forEach((image) => {
            console.log(image);
            formData.append('multipartFile', image);
          });
        }
      }
    });
    delete uploadTargetData.images;
    uploadTargetData.journeyId = journeyId;
    if (placeId !== '') {
      uploadTargetData.placeId = Number(placeId);
    }
    uploadTargetData.placeTime = uploadTargetData.placeTime.toISOString();
    console.log(uploadTargetData);
    formData.append('request', new Blob([JSON.stringify(uploadTargetData)], { type: 'application/json' }));
    return [formData, submitPossible];
  }, [placeInfo]);

  const actions = useMemo(() => ({
    updateData(key, value) {
      setPlaceInfo((prev) => ({ ...prev, [key]: value }));
    },
    updateMultiData(keyList, valueList) {
      setPlaceInfo((prev) => {
        let newPlaceInfo = { ...prev };
        keyList.forEach((element, index) => {
          newPlaceInfo = {
            ...newPlaceInfo,
            [element]: valueList[index],
          };
        });
        return newPlaceInfo;
      });
    },
    uploadData(params) {
      const [formData, submitPossible] = makeFormData(params.journeyId);
      if (submitPossible) {
        const url = '/place';
        authAxios
          .post(url, formData)
          .then((res) => {
            window.alert('제출이 성공적으로 완료되었습니다.');
            navigation(`/journey/${params.journeyId}`);
          })
          .catch((err) => {
            window.alert(`${err}로 인해 제출에 실패하였습니다.`);
            navigation(`/journey/${params.journeyId}`);
          });
      }
    },
    updateServerData(params) {
      const { placeId } = params;
      const [formData, submitPossible] = makeFormData(placeInfo.journeyId, placeId);
      if (submitPossible) {
        const url = '/place';
        authAxios
          .put(url, formData)
          .then((res) => {
            window.alert('제출이 성공적으로 완료되었습니다.');
            navigation(`/journey/${placeInfo.journeyId}`);
          })
          .catch((err) => {
            window.alert(`${err}로 인해 제출에 실패하였습니다.`);
            navigation(`/journey/${placeInfo.journeyId}`);
          });
      }
    },
  }), [placeInfo]);

  return (
    <PlaceInfoActionsContext.Provider value={actions}>
      <PlaceInfoValueContext.Provider value={placeInfo}>
        {children}
      </PlaceInfoValueContext.Provider>
    </PlaceInfoActionsContext.Provider>
  );
}

const usePlaceInfoValue = () => {
  const value = useContext(PlaceInfoValueContext);
  if (value === undefined) {
    throw new Error(
      'usePlaceInfoValue should be used within PlaceInfoProvider',
    );
  }
  return value;
};

const usePlaceInfoActions = () => {
  const actions = useContext(PlaceInfoActionsContext);
  if (actions === undefined) {
    throw new Error(
      'usePlaceInfoActions should be used within PlaceInfoProvider',
    );
  }
  return actions;
};

export { usePlaceInfoValue, usePlaceInfoActions };
export default PlaceInfoProvider;
