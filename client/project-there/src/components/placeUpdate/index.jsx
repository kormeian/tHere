import React, { useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import styled from 'styled-components';
import ImageInputCarousel from '../placeUpload/ImageInputCarousel';
import PlaceInfoHolder from '../placeUpload/PlaceInfoHolder';
import { usePlaceInfoActions, usePlaceInfoValue } from '../../contexts/PlaceInfoContext';
import { authAxios, baseAxios } from '../../lib/utills/customAxios';

const StyledPlaceUploadHolder = styled.div`
  width: 70vw;
  height: 70vh;
  position: relative;
  left: 15vw;
  top: 15vh;
  display: flex;
  border: 1px solid #dde4e5;
  .submit{
    position: absolute;
    right: 30px;
    bottom: 20px;
    width: 88px;
    height: 39px;
    border-radius: 20px;
    background-color: var(--color-green100);
    letter-spacing: -5%;
    color:white;
  }
  .cancle{
    position: absolute;
    right: 140px;
    bottom: 20px;
    width: 88px;
    height: 39px;
    border-radius: 20px;
    background-color: var(--color-gray400);
    letter-spacing: -5%;
    border: none;
  }
`;

const keyList = [
  'latitude',
  'longitude',
  'title',
  'text',
  'placeCategory',
  'addressName',
  'region1',
  'region2',
  'region3',
  'region4',
  'placeTime',
  'placeName',
  'journeyId',
  'images',
];

export default function PlaceUpdate() {
  const { updateServerData, updateMultiData } = usePlaceInfoActions();
  const navigation = useNavigate();
  const params = useParams();
  const placeInfo = usePlaceInfoValue();

  const handleSubmitClick = async (e) => {
    e.preventDefault();
    updateServerData(params);
  };
  const handleCancleClick = () => {
    navigation(-1);
  };

  const InitialSetting = async () => {
    const placeId = params?.placeId;
    const url = `place?placeId=${placeId}`;
    const { data } = await authAxios.get(url);
    const { imageUrls } = data;

    // 이미지 url들을 이용하여 서버에서 이미지 파일들을 받아줌
    const imageFilesRequest = await Promise.all(
      imageUrls.map((imageUrl) => {
        const tmpUrl = imageUrl.split('/');
        const imageRequestUrl = `image/file?imageUrl=${tmpUrl[tmpUrl.length - 1]}`;
        return baseAxios.get(imageRequestUrl, { responseType: 'arraybuffer' });
      }),
    );
    const imageFiles = imageFilesRequest.map((request) => {
      const baseData = request.data;
      const imageBolb = new Blob([baseData], { type: 'image/png' });
      const imageFile = new File([imageBolb], '이미지수정.png');
      return imageFile;
    });

    const valueList = keyList.map((key) => {
      if (key === 'placeTime') {
        const time = data[key];
        const dateTime = new Date(time);
        dateTime.setHours(dateTime.getHours() + 9);
        return dateTime;
      }
      if (key === 'images') {
        return imageFiles;
      }
      return data[key];
    });
    updateMultiData(keyList, valueList);
  };

  useEffect(() => {
    InitialSetting();
  }, []);

  useEffect(() => {
    console.log(placeInfo);
  }, [placeInfo]);

  return (
    <StyledPlaceUploadHolder>
      <ImageInputCarousel />
      <PlaceInfoHolder />
      <button className="submit" type="button" onClick={handleSubmitClick}>
        등록
      </button>
      <button className="cancle" type="button" onClick={handleCancleClick}>
        취소
      </button>
    </StyledPlaceUploadHolder>
  );
}
