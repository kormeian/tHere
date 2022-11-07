import React from 'react';
import PlaceUpdate from '../components/placeUpdate';
import PlaceInfoProvider from '../contexts/PlaceInfoContext';

export default function PlaceUpdatePage() {
  return (
    <PlaceInfoProvider>
      <PlaceUpdate />
    </PlaceInfoProvider>
  );
}
