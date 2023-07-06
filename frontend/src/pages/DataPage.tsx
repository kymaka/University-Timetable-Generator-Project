import React, { Fragment, useState, useEffect } from 'react';
import Room from "../types/Room";
import RoomService from '../services/RoomService';
import RoomComponent from '../components/TypeComponents/RoomComponent';
import { RoomType } from '../enums/RoomType';
import { SuperTable } from '../components/Table/Table';
import { useLocation } from 'react-router-dom'

function HeaderView() {
  const location = useLocation();
  console.log(location.pathname);
  return location.pathname
}

function DataPage() {
  //  const [rooms, setRooms] = useState<Room[]>([]);

  //  useEffect(() => {
  //    RoomService.getAll().then((data) => {
  //      setRooms(data.data);
  //    })
  //  })
  const path = HeaderView();
  if (path === '/rooms') {
    return (
      <>
        <h1>Rooms</h1>
        <SuperTable type={'room'} />
      </>
    );
  } else if(path === '/groups') {
    return (
      <>
        <h1>Groups</h1>
        <SuperTable type={'group'} />
      </>
    );
  } else if (path === '/subjects') {
    return (
      <>
        <h1>Subjects</h1>
        <SuperTable type={'subject'} />
      </>
    );
  } else if (path === '/teachers') {
    return (
      <>
        <h1>Teachers</h1>
        <SuperTable type={'teacher'} />
      </>
    );
  } else {
    return (
      <>
        <h1>Time Slots</h1>
        <SuperTable type={'timeslot'} />
      </>
    );
  }
}

export default DataPage;