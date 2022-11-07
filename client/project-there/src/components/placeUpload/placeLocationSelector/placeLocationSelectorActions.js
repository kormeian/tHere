const coord2AddressSearch = (lng, lat) => new Promise((resolve, reject) => {
  const geocoder = new window.kakao.maps.services.Geocoder();
  geocoder.coord2Address(lng, lat, (result, status) => {
    if (status === window.kakao.maps.services.Status.OK) {
      resolve(result[0].address.address_name);
    } else {
      reject(status);
    }
  });
});
const addressToPlaceNameSearch = (address) => new Promise((resolve, reject) => {
  const ps = new window.kakao.maps.services.Places();
  ps.keywordSearch(address, (result, status) => {
    if (status === window.kakao.maps.services.Status.OK) {
      resolve(result);
    } else {
      resolve([]);
    }
  });
});
const filterSearchPlaceList = (targetList) => {
  let result = [...targetList];
  result = result.reduce((acc, cur) => {
    let include = false;
    const placeName = cur.place_name;
    const placeAddressName = cur.address_name;
    const placeLat = cur.y;
    const placeLng = cur.x;
    acc?.forEach((place) => {
      if (place[0] === placeName) {
        include = true;
      }
    });
    if (!include) {
      return [...acc, [placeName, placeAddressName, placeLat, placeLng]];
    }
    return acc;
  }, []);

  return result;
};
const makePlaceInfoLocation = (selectedInfo) => {
  const keyList = [
    'placeName',
    'addressName',
    'latitude',
    'longitude',
    'region1',
    'region2',
    'region3',
    'region4',
  ];
  const valueList = [
    selectedInfo[0],
    selectedInfo[1],
    selectedInfo[2],
    selectedInfo[3],
    selectedInfo[1].split(' ')[0],
    selectedInfo[1].split(' ')[1],
    selectedInfo[1].split(' ')[2],
    selectedInfo[1].split(' ')[3],
  ];
  return [keyList, valueList];
};
const findPointLocation = async (pointAddress) => {
  const results = await Promise.all(
    pointAddress?.map((address) => addressToPlaceNameSearch(address)));
  const newResult = results.flat(1);
  let resultData = [];
  if (newResult.length !== 0) {
    resultData = newResult?.map((result) => {
      const placeName = result.place_name;
      const addressName = result.address_name;
      const latitude = result.y;
      const longitude = result.x;
      return [placeName, addressName, latitude, longitude];
    });
  }
  return resultData;
};
export {
  coord2AddressSearch,
  filterSearchPlaceList,
  makePlaceInfoLocation,
  findPointLocation,
};
