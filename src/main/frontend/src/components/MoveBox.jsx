import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { AnimatePresence, motion } from "framer-motion";
import axios from "axios";

import styles from "./styles/moveBox.module.css"

import serializeError from "../functions/serializeError";
import apiRequest from "../functions/apiRequest";
import Loading from "./Loading";

export default function MoveBox({parentStyles, isMoveOn, setIsMoveOn, photos, afterMove, setCreatingAlbum, update}){
  const params = useParams();
  const [albums, setAlbums] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  const navigate = useNavigate();

  const movePhotos = async({e}) => {
    const params = {
      photoIds: photos.toString(),
      albumId: e.target.id,
    };
    try{
      await axios.put(
        `/albums/${params.albumId}/photos`, null,
        {
          params: params,
          headers:{Authorization: `Bearer ${sessionStorage.getItem("AccessToken")}`}
        }
      )

      afterMove(e.target.id)
      getAlbums()
    } catch (error){
      const serializableError = serializeError(error);
      navigate("/error", {state:{error:serializableError}})
    }
  }

  const getAlbums = async() => {
    try{
      const res = await axios.get(`/albums`,
      {
        headers:{Authorization: `Bearer ${sessionStorage.getItem("AccessToken")}`}
      })
      setAlbums(res.data)
    } catch (error){
      const serializableError = serializeError(error);
      navigate("/error", {state:{error:serializableError}})
    }
  }

  useEffect(()=>{
    apiRequest(getAlbums, setIsLoading);
  }, [update])

  return(
    <AnimatePresence>
    {isMoveOn&&
    <div 
      className={styles.moveBoxCon} 
      style={parentStyles}
    >
      <div className={styles.tri}/>
      <motion.div 
        className={styles.moveBox}
        initial={{height:"0px"}}
        animate={{
          height:"fit-content",
          transition: {
            type: "spring"
          }
        }}
        exit={{height:"0px"}}
      >
        <div>
          <div>
            <div 
              className={`${styles.createBox}`} 
              onClick={()=>{
                setCreatingAlbum(true)
                setIsMoveOn(false)
              }}>+
            </div>
          </div> 
        <div><h3>새 앨범 생성</h3></div>
        <div />
      </div>
      {albums.map(album=>{
        return Number(params.albumId)!==album.albumId&&<div key={album.albumId}>
          <div>
          <div className={styles.albumThumbs}>
            {album.thumbUrls.map((thumb, idx)=>{
              return<img key={idx} alt={album.albumName} src={`data:image/png;base64, ${thumb}`} />
            })}
          </div>
          </div>
          <div>
            <h3>{album.albumName}</h3>
            {album.createdAt?.slice(0, 10)} | {album.count}장
          </div>
          <div 
            id={album.albumId}
            className="material-symbols-outlined"
            onClick={e=>apiRequest(movePhotos, setIsLoading, {e})}
          >arrow_circle_right</div>
        </div>
      })}
    </motion.div>
    <Loading isLoading={isLoading}/>
    </div>}
    </AnimatePresence>
  );
}