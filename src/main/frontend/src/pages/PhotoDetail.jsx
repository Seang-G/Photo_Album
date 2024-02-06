import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import axios from "axios";

import styles from "./styles/photoDetail.module.css"

import MoveBox from "../components/MoveBox";
import InputBox from "../components/InputBox";
import serializeError from "../functions/serializeError";
import apiRequest from "../functions/apiRequest";
import Loading from "../components/Loading";

export default function PhotoDetail(){
  const params = useParams();
  const navigate = useNavigate();

  const [photo, setPhoto] = useState({});
  const [loaded, setLoaded] = useState(false);
  const [isMoveOn, setIsMoveOn] = useState(false);
  const [creatingAlbum, setCreatingAlbum] = useState(false)
  const [update, setUpdate] = useState(0);
  const [isLoading, setIsLoading] = useState(false);

  

  const getPhotoDetail = async() => {
    try{
      const res = await axios.get(
        `/albums/${params.albumId}/photos/${params.photoId}`,
        {
          headers:{Authorization: `Bearer ${sessionStorage.getItem("AccessToken")}`}
        }
      )
      setPhoto(res.data)
      setLoaded(true)
    } catch (error){
      const serializableError = serializeError(error);
      navigate("/error", {state:{error:serializableError}})
    }
  }

  const downloadPhoto = async() => {
    try{
      const res = await axios.get(
        `/albums/${params.albumId}/photos/download?photoIds=${params.photoId}`,
        {
          headers:{Authorization: `Bearer ${sessionStorage.getItem("AccessToken")}`},
          responseType: "blob"
        }
      )

      const url = window.URL.createObjectURL(new Blob([res.data]))
      const a = document.createElement('a');
      a.href = url;
      a.download = `${photo.fileName}.png`;
      a.click();
    } catch (error){
      const serializableError = serializeError(error);
      navigate("/error", {state:{error:serializableError}})
    }
  }

  const deletePhoto = async() => {
    try {
      await axios.delete(
        `/albums/${params.albumId}/photos?photoIds=${params.photoId}`,
        {
          headers:{Authorization: `Bearer ${sessionStorage.getItem("AccessToken")}`},
        }
      )
      alert("삭제되었습니다.")
      navigate("./..")
    } catch (error){
      const serializableError = serializeError(error);
      navigate("/error", {state:{error:serializableError}})
    }
  }

  const createAlbum = async({newAlbumName}) => {
    try{
      await axios.post("/albums",
        {albumName:newAlbumName},
        {headers: {
          Authorization: `Bearer ${sessionStorage.getItem("AccessToken")}`
        }}
      )
      setCreatingAlbum(false)
      setUpdate(pre=>pre+1)
    } catch (error){
      const serializableError = serializeError(error);
      navigate("/error", {state:{error:serializableError}})
    }
  }
  
  const convertFilename = () => {
    if(loaded){
      let newName = photo.fileName
      if (newName.length >= 16) {
        newName = newName.substr(0, 16) + "..."
      }
      return newName
    }
  }

  const convertDate = () => {
    if(loaded){
      let date = photo.uploadedAt.substr(0, 10)
      date += ", " + photo.uploadedAt.substr(11, 5)
      return date
    }
  }

  const convertSize = () => {
    if(loaded){
      let size = Number(photo.fileSize)
      let temp = size/1024
      let cnt = 0
      while (temp>=1) {
        temp /= 1024
        cnt++
      }

      return String(temp*1024).substring(0, 5) +" "+ ["KB", "MB"][cnt-1]
    }
  }

  useEffect(()=>{
    apiRequest(getPhotoDetail, setIsLoading)
  }, [])

  return (
    photo&&<div className={styles.photoDetail}>
      <div className={styles.back} onClick={()=>navigate("./..")}>
        <span className="material-symbols-outlined">
          arrow_back
        </span>
        {" "}앨범 목록으로 돌아가기
      </div>

      <div className={styles.imgCon}>
        {photo.imageFile!==undefined&&<img 
          alt={photo.fileName} 
          src={`data:image/png;base64, ${photo.imageFile}`}/>}
      </div>
      <div className={`${styles.symbols} material-symbols-outlined`}>

        <motion.div 
          onClick={()=>setIsMoveOn(pre=>!pre)}
          whileHover={{fontWeight:700}}
        >
          drive_file_move</motion.div>

        <motion.div 
          onClick={()=>apiRequest(downloadPhoto, setIsLoading)}
          whileHover={{fontWeight:700}}
        >
          download</motion.div>

        <motion.div 
          onClick={()=>{
            if(window.confirm("정말로 사진을 삭제하시겠습니까?"))
              apiRequest(deletePhoto, setIsLoading)
          }}
          whileHover={{fontWeight:700}}
        >
          delete</motion.div>

      </div>
      <hr />
      <div className={styles.info}>
        <h2>이미지 정보</h2>
        <div>
          <div className={styles.description}>{convertFilename()}</div>
          <hr />
          <div className={styles.about}>파일명</div>
        </div>
        <div>
          <div className={styles.description}>
            {convertDate(photo.uploadedAt)}
          </div>
          <hr />
          <div className={styles.about}>업로드 날짜</div>
        </div>
        <div>
          <div className={styles.description}>{convertSize(photo.fileSize)}</div>
          <hr />
          <div className={styles.about}>파일 용량</div>
        </div>
      </div>
      <MoveBox 
        isMoveOn={isMoveOn}
        setIsMoveOn={setIsMoveOn}
        photos={[params.photoId]}
        afterMove={(nextAlbum)=>{
          navigate(`/albums/${nextAlbum}/${params.photoId}`)
        }}
        setCreatingAlbum={setCreatingAlbum}
        parentStyles={{ right:"800px", top:"200px"}}
        update={update}
      />
      <InputBox 
        isOn = {creatingAlbum}
        setIsOn = {setCreatingAlbum}
        title = "새 앨범 생성"
        buttonSubject = "생성"
        onClick = {(value)=>apiRequest(createAlbum, setIsLoading, {value})}
      />
      <Loading isLoading={isLoading}/>
    </div>
  );
}