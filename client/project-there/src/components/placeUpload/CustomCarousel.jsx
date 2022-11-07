import React, { useState, useEffect, useContext } from 'react';
import 'react-responsive-carousel/lib/styles/carousel.min.css';
import { Carousel } from 'react-responsive-carousel';
import Resizer from 'react-image-file-resizer';
import exifr from 'exifr';
import styled from 'styled-components';
import CustomDropZone from './CustomDropZone';
import { usePlaceInfoActions, usePlaceInfoValue } from '../../contexts/PlaceInfoContext';
import CarouselItem from './CarouselItem';

const resizeFileToBase64 = (file) => new Promise((resolve) => {
  Resizer.imageFileResizer(
    file,
    600,
    600,
    'JPEG',
    100,
    0,
    (uri) => {
      resolve(uri);
    },
    'base64',
  );
});
const resizeFileToFile = (file) => new Promise((resolve) => {
  Resizer.imageFileResizer(
    file,
    600,
    600,
    'JPEG',
    100,
    0,
    (uri) => {
      resolve(uri);
    },
    'file',
  );
});

const StyledCarousel = styled(Carousel)`
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  .carousel-slider{
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }
`;

export default function CustomCarousel({ containerRef }) {
  const [acceptedImages, setAcceptedImages] = useState([]);
  const [, setRejectedImages] = useState([]);
  const [resizedImages, setResizedImages] = useState([]);
  const { updateData } = usePlaceInfoActions();
  const [containerHeight, setContainerHeight] = useState(300);
  const placeInfo = usePlaceInfoValue();

  const addAcceptedImages = (preImages, curImages) => {
    setAcceptedImages([...preImages, ...curImages]);
  };
  const addRejectedImages = (curImages) => {
    setRejectedImages([...curImages]);
  };

  useEffect(() => {
    if (placeInfo?.images?.length !== 0) {
      setAcceptedImages(placeInfo?.images);
    }
  }, [placeInfo?.images?.length]);

  useEffect(() => {
    Promise.all(acceptedImages?.map((image) => resizeFileToBase64(image))).then((result) => {
      setResizedImages(result);
    });
    Promise.all(acceptedImages?.map((image) => resizeFileToFile(image))).then((result) => {
      updateData('images', result);
    });
    Promise.all(acceptedImages?.map((image) => exifr.parse(image)))
      .then((result) => {
        let placeVisitedTime = new Date();
        const presentTime = placeVisitedTime;
        const imageTakenLocations = [];
        const findDuplicate = [];
        result.forEach((info) => {
          if (info) {
            const { CreateDate, latitude, longitude } = info;
            if (CreateDate) {
              placeVisitedTime = CreateDate < placeVisitedTime ? CreateDate : placeVisitedTime;
            }
            if (latitude && longitude) {
              if (!findDuplicate.includes(`${latitude}${longitude}`)) {
                findDuplicate.push(`${latitude}${longitude}`);
                imageTakenLocations.push({ lat: latitude, lng: longitude });
              }
            }
          }
        });
        if (imageTakenLocations.length !== 0) {
          updateData('imageTakenLocations', imageTakenLocations);
        }
        if (presentTime !== placeVisitedTime) {
          updateData('placeTime', placeVisitedTime);
        }
      });
  }, [acceptedImages]);

  useEffect(() => {
    if (containerRef) {
      setContainerHeight(containerRef.current.offsetHeight);
    }
  }, [containerRef]);

  return (
    <StyledCarousel
      autoPlay={false}
      infiniteLoop
      showThumbs={false}
    >
      {resizedImages?.map((imageUrl, index) => (
        <CarouselItem
          key={imageUrl}
          src={imageUrl}
          imgControl={[acceptedImages, setAcceptedImages]}
          number={index}
          height={containerHeight}
        />
      ))}
      <CustomDropZone
        info={[acceptedImages, addAcceptedImages, addRejectedImages]}
        height={containerHeight}
      />
    </StyledCarousel>
  );
}
