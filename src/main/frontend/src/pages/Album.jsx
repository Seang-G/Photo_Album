import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom"
import { AnimatePresence, motion, useAnimationControls } from "framer-motion";

import styles from "./styles/album.module.css"
import axios from "axios";
import Photo from "../components/Photo";
import MoveBox from "../components/MoveBox";
import InputBox from "../components/InputBox";
import serializeError from "../functions/serializeError";
import apiRequest from "../functions/apiRequest";
import Loading from "../components/Loading";


const sortMap = {
  "생성 날짜순": "byDate",
  "A-Z 이름순": "byName"
}

export default function Album() {
  const params = useParams();
  const albumId = isNaN(params.album)?-1:params.albumId;
  const [photos, setPhotos] = useState([]);
  const [currentAlbum, setCurrentAlbum] = useState({});
  const [checkedPhotos, setCheckedPhotos] = useState([]);
  const [sort, setSort] = useState("생성 날짜순");
  const [order, setOrder] = useState("desc");
  const [changed, setChanged] = useState(false);
  const [isAllSelected, setIsAllSelected] = useState(false);
  const [changingAlbumName, setChangingAlbumName] = useState(false);
  const [isMoveOn, setIsMoveOn] = useState(false);
  const [update, setUpdate] = useState(0);
  const [creatingAlbum, setCreatingAlbum] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const navigate = useNavigate();
  const controls = useAnimationControls();

  const functionStyle = {
    cursor:checkedPhotos.length?"pointer":"default",
    opacity:0.5
  }

  const getPhotos = async() => {
    try{
      const res = await axios.get(`/albums/${albumId}/photos`,
      {
        headers:{Authorization: `Bearer ${sessionStorage.getItem("AccessToken")}`},
        params: {sort:sortMap[sort], order}
      })
      setPhotos(pre=>[...res.data])
      setChanged(false)
    } catch (error){
      const serializableError = serializeError(error);
      navigate("/error", {state:{error:serializableError}})
    }
  }

  const getAlbum = async() => {
    try{
      const res = await axios.get(`/albums/${albumId}`,
      {
        headers:{Authorization: `Bearer ${sessionStorage.getItem("AccessToken")}`}
      })
      setCurrentAlbum(res.data)
    } catch (error){
      const serializableError = serializeError(error);
      navigate("/error", {state:{error:serializableError}})
    }
  }

  const changeAlbumName = async({newAlbumName}) => {
    // setIsLoading(true)
    try {
      await axios.put(`/albums/${albumId}`,
      {
        albumName:newAlbumName
      },
      {
        headers:{Authorization: `Bearer ${sessionStorage.getItem("AccessToken")}`}
      })
      setChangingAlbumName(false)
      getAlbum()
    } catch (error) {
      const serializableError = serializeError(error);
      navigate("/error", {state:{error:serializableError}})
    }
    // setIsLoading(false)
  }

  const deletePhotos = async() => {
    
    if(checkedPhotos.length){
      // setIsLoading(true)
      try {
        await axios.delete(
          `/albums/${albumId}/photos?photoIds=${checkedPhotos.toString()}`,
          {
            headers:{Authorization: `Bearer ${sessionStorage.getItem("AccessToken")}`},
          }
        )
        getPhotos()
        setCheckedPhotos([])
      } catch (error) {
        const serializableError = serializeError(error);
        navigate("/error", {state:{error:serializableError}})
      }
      // setIsLoading(false)
    }
  }

  const downloadPhotos = async() => {
    if(checkedPhotos.length){
      try {
        const res = await axios.get(
          `/albums/${albumId}/photos/download?photoIds=${checkedPhotos.toString()}`,
          {
            headers:{Authorization: `Bearer ${sessionStorage.getItem("AccessToken")}`},
            responseType: "blob"
          }
        )

        const url = window.URL.createObjectURL(new Blob([res.data]))
        const a = document.createElement('a');
        a.href = url;
        if (checkedPhotos.length === 1) {
          for( var photo of photos) {
            if (photo.photoId === checkedPhotos[0]) {
              a.download = photo.originalUrl.split("/").pop()
              break
            }
          }
        } else {
          a.download = 'Photos.zip';
        }
        a.click();
        setCheckedPhotos([])
      } catch (error) {
        const serializableError = serializeError(error);
        navigate("/error", {state:{error:serializableError}})
      }
      // setIsLoading(false)
    }
  }

  const createAlbum = async({newAlbumName}) => {
    // setIsLoading(true)
    try {
      await axios.post("/albums",
        {albumName:newAlbumName},
        {headers: {
          Authorization: `Bearer ${sessionStorage.getItem("AccessToken")}`
        }}
      )
      setCreatingAlbum(false)
      setUpdate(pre=>pre+1)
    } catch (error) {
      const serializableError = serializeError(error);
      navigate("/error", {state:{error:serializableError}})
    }
    // setIsLoading(false)
  }

  const selectAll = () => {
    if (isAllSelected) setCheckedPhotos([])
    else {
      let allPhotoIds = []
      for (var photo of photos) {
        allPhotoIds.push(photo.photoId)
      }
      setCheckedPhotos(allPhotoIds)
    }
  }

  useEffect(()=>{
    if(checkedPhotos.length === photos.length) setIsAllSelected(true)
    else setIsAllSelected(false)
    setIsMoveOn(false)
  }, [checkedPhotos, photos])

  useEffect(()=>{
    apiRequest(getAlbum, setIsLoading)
    apiRequest(getPhotos, setIsLoading)
  }, [])

  useEffect(()=>{
    if(checkedPhotos.length) {
      controls.start({opacity:1})
    } else {
      controls.start({opacity:0.5})
    }
  }, [controls, checkedPhotos])

  return(
    <div className={styles.album}>
      <div className={styles.back} onClick={()=>navigate("/albums")}>
        <span className="material-symbols-outlined">
          arrow_back
        </span>
        {" "}앨범 목록으로 돌아가기
      </div>

      <div className={styles.title} onClick={()=>setChangingAlbumName(true)}>
        <h1 >{currentAlbum.albumName}</h1>
        <span>
          <span className="material-symbols-outlined">
            arrow_back
          </span> 
          앨범명을 변경하려면 클릭하세요
        </span>
      </div>

      <div className={styles.info}>
        <button onClick={()=>navigate("./upload")}>사진 추가</button>
        <span>{currentAlbum.createdAt?.slice(0, 10)}</span>
        <span style={{color:"lightslategray", fontSize:"30px", fontWeight:100}}>|</span>
        <span>{currentAlbum.count}장</span>
      </div>
      
      <div className={styles.functions}>
        <div onClick={selectAll}>
          <span 
            className="material-symbols-outlined"
            style={{
              fontSize:"1.6rem",
              marginLeft: "10px",
              color:isAllSelected?"black":"lightslategray",
            }}
          >
            check_circle
          </span>
          {" "}전체 선택하기
        </div>
        <div>
          <div className={`${styles.symbols} material-symbols-outlined`}>
            <div>
              <motion.div
                style={functionStyle}
                onClick={()=>{if (checkedPhotos.length) setIsMoveOn(pre=>!pre)}}
                animate={controls}
                whileHover={{
                  fontWeight:700,
                }}
              >drive_file_move</motion.div>
            </div>
            
            <motion.div 
              onClick={()=>apiRequest(downloadPhotos, setIsLoading)}
              style={functionStyle}
              animate={controls}
              whileHover={{
                fontWeight:700,
              }}
            >download</motion.div>

            <motion.div
              style={functionStyle}
              onClick={()=>{
                if(window.confirm("정말로 사진을 삭제하시겠 습니까?"))
                  apiRequest(deletePhotos, setIsLoading)
              }}
              animate={controls}
              whileHover={{
                fontWeight:700,
              }}
            >delete</motion.div>
          </div>
          {changed&&<span
            className={`${styles.apply} material-symbols-outlined`}
            onClick={()=>apiRequest(getPhotos, setIsLoading)}
          >
            autorenew
          </span>}

        <AnimatePresence mode="popLayout">
          <motion.span 
            key={sort}
            onClick={(e)=>{
              if(e.target.innerHTML === sort){
                setSort(pre=>sort==="생성 날짜순"?"A-Z 이름순":"생성 날짜순")
                setChanged(true)
              }
            }}
            style={{
              display:"inline-block",
              width:"100px",
              textAlign:"right",
              marginRight: "5px",
            }}
            initial={{ zIndex:0, opacity: 0, y: 10 }}
            animate={{ zIndex:1,opacity: 1, y: 0 }}
            exit={{ zIndex:0,opacity: 0, y: -10 }}
          >
            {sort}
          </motion.span>
          </AnimatePresence>
          <span 
            className={`${styles.order} material-symbols-outlined`}
            onClick={()=>{
              setOrder(pre=>pre==="desc"?"asc":"desc")
              setChanged(true)
            }}
            style={{
              rotate:`${order==="desc"?0:180}deg`,
              transition: "rotate 0.3s"
            }}
          >arrow_downward</span>
        </div>
      </div>
      
      <hr />
      
      <MoveBox 
        isMoveOn={isMoveOn}
        setIsMoveOn={setIsMoveOn}
        photos={checkedPhotos}
        afterMove={()=>{
          setCheckedPhotos([])
          apiRequest(getPhotos, setIsLoading)
        }}
        setCreatingAlbum={setCreatingAlbum}
        parentStyles={{"right":"630px"}}
        update={update}
      />
      <div className={styles.photos}>
        {photos.map((photo)=>{

          return<div key={photo.photoId}>
            <Photo 
              photoId={photo.photoId}
              checkedPhotos={checkedPhotos}
              setCheckedPhotos={setCheckedPhotos}
            />
            <img 
              src={`data:image/png;base64, ${photo.imageFile}`} 
              alt={photo.fileName}
            />
            <div className={styles.photoName}>{photo.fileName}</div>
          </div>
        })}
      </div>

      <InputBox 
        isOn={changingAlbumName}
        setIsOn={setChangingAlbumName}
        title="앨범명 변경"
        buttonSubject="변경"
        onClick={(value)=>apiRequest(changeAlbumName, setIsLoading, {value})}
      />

      <InputBox 
        isOn={creatingAlbum}
        setIsOn={setCreatingAlbum}
        title="새 앨범 생성"
        buttonSubject="생성"
        onClick={(value)=>
          apiRequest(createAlbum, setIsLoading, {value})}
      />
      <Loading isLoading={isLoading}/>
    </div>
  )
}