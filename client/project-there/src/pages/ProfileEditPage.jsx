import React from 'react';
import { useParams } from 'react-router-dom';
import ProfileEdit from '../components/profileEdit/ProfileEdit';

function ProfileEditPage() {
  const { memberId } = useParams();

  return <ProfileEdit editMode={!!memberId} />;
}

export default ProfileEditPage;
