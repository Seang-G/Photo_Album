import { useState } from "react";
import styles from "./styles/folder.module.css"
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { AnimatePresence, motion } from "framer-motion";
import serializeError from "../functions/serializeError";
import Loading from "./Loading";
import InputBox from "./InputBox";
import apiRequest from "../functions/apiRequest";

export default function Folder({isNew=false, albumName, albumCnt, thumbUrls, albumId, getAlbums}) {
  const [creatingAlbum, setCreatingAlbum] = useState(false);
  const [isHover, setIsHover] = useState(false);
  const [isLoading, setIsLoading] = useState(false);


  const navigate = useNavigate();

  const createAlbum = async({newAlbumName}) => {
    try{
      await axios.post("/albums",
        {albumName:newAlbumName},
        {headers: {
          Authorization: `Bearer ${sessionStorage.getItem("AccessToken")}`
        }}
      )
      setCreatingAlbum(false)
      getAlbums()
    } catch (error){
      const serializableError = serializeError(error);
      navigate("/error", {state:{error:serializableError}})
    }
  }

  const deleteAlbum = async() => {
    if(window.confirm(`정말로 삭제하시겠습니까?`)){
      try{
        await axios.delete(`/albums/${albumId}`,
          {headers: {
            Authorization: `Bearer ${sessionStorage.getItem("AccessToken")}`
          }}
        )
        getAlbums()
      } catch (error){
        const serializableError = serializeError(error);
        navigate("/error", {state:{error:serializableError}})
      }
    }
  }

  return (
    <div
      onMouseEnter={()=>setIsHover(true)}
      onMouseLeave={()=>setIsHover(false)}
    >
    <motion.div 
      className={styles.cover}
      initial={{opacity:1}}
      animate={{opacity:isHover&&!isNew?0.6:1}}
    >
      <div className={styles.folderTop} />
      {!isNew&&<span>
        <strong>{albumCnt} </strong>
        Photos
      </span>}
      <div className={styles.folder} >
        {!isNew&&<div className={styles.thumbs} onClick={()=>navigate(`/albums/${albumId}`)}>
          {thumbUrls.map((thumb, idx) => {
            return(<div key={idx}>
              <img src={`data:image/png;base64, ${thumb}`} alt={albumName} />
            </div>)
          })}
        </div>}

        {isNew&&<motion.div 
          className={styles.create} 
          onClick={()=>setCreatingAlbum(true)}
          initial={{
            background: "radial-gradient(circle, rgba(255,255,255,1) 0%, rgba(255,255,255,1) 0%)"
          }}
          whileHover={{
            borderRadius:"100px",
            background: "radial-gradient(circle, rgba(245,240,227,1) 0%, rgba(255,255,255,1) 100%)"
          }}
        >
          <strong>
            +
          </strong><br />
          새 앨범 생성
        </motion.div>}
      </div>

    </motion.div>
    <div className={styles.albumName}>
      <h3>{albumName}</h3>
    </div>
    
    <AnimatePresence>
      {isHover&&!isNew&&
        <motion.div 
          className={styles.action}
          initial={{opacity:0}}
          animate={{opacity:1}}
          exit={{opacity:0}}
        >
          <motion.div 
            onClick={()=>navigate(`./${albumId}`)}
            whileHover={{scale:1.05}}
          >
            앨범 열기
          </motion.div>
          <motion.div 
            onClick={
              ()=>apiRequest(deleteAlbum, setIsLoading)
            } 
            className="material-symbols-outlined"
            whileHover={{color:"rgb(255, 0, 0)"}}
          >
            delete
          </motion.div>
        </motion.div>
      }
    </AnimatePresence>
    <Loading isLoading={isLoading}/>
    <InputBox
      isOn={creatingAlbum}
      setIsOn={setCreatingAlbum}
      title="새 앨범 생성"
      buttonSubject="생성"
      onClick={(newAlbumName)=>apiRequest(createAlbum, setIsLoading, {newAlbumName})}
    />
    </div>
  );
}