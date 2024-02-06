import axios from "axios"
import { useEffect, useState } from "react"

import styles from "./styles/albums.module.css"
import Folder from "../components/Folder"
import { useNavigate } from "react-router-dom"
import serializeError from "../functions/serializeError"
import apiRequest from "../functions/apiRequest"
import Loading from "../components/Loading"
import { AnimatePresence, motion } from "framer-motion"

const sortMap = {
  "생성 날짜순": "byDate",
  "A-Z 이름순": "byName"
}

export default function AlbumList() {

  const [albums, setAlbums] = useState([])
  const [sort, setSort] = useState("생성 날짜순")
  const [order, setOrder] = useState("desc")
  const [keyword, setKeyword] = useState("")
  const [changed, setChanged] = useState(false)
  const [isLoading, setIsLoading] = useState(false)

  const navigate = useNavigate()


  const getAlbums = async({keyword="", sort="", order=""}) => {
    // setIsLoading(true)
    try{
      const res = await axios.get(`/albums`, {
        headers: {Authorization:`Bearer ${sessionStorage.getItem("AccessToken")}`},
        params: {keyword, sort:sortMap[sort], order}
      })
      setAlbums(pre=>[...res.data])
    } catch (error) {
      const serializableError = serializeError(error)
      navigate("/error", {state:{error:serializableError}})
    }
    // setIsLoading(false)
  }

  useEffect(()=>{
    apiRequest(getAlbums, setIsLoading, {keyword, sort, order})
  }, [])

  return(
    <div className={styles.albumList}>
      <h1>내 사진첩</h1>

      <div className={styles.upBar}>
        <h2>Galleries</h2>
        <div>
          <span>
            <AnimatePresence mode="popLayout">
            <motion.span 
              key={sort}
              onClick={(e)=>{
                if(e.target.innerHTML === sort){
                  setSort(pre=>sort==="생성 날짜순"?"A-Z 이름순":"생성 날짜순")
                  setChanged(true)
                }
              }}
              initial={{ zIndex:0, opacity: 0, y: 10 }}
              animate={{ zIndex:1,opacity: 1, y: 0 }}
              exit={{ zIndex:0,opacity: 0, y: -10 }}
            >
              {sort}
            </motion.span>
            </AnimatePresence>
            <span 
              className={`${styles.orderArrow} material-symbols-outlined`}
              onClick={()=>{
                setOrder(pre=>pre==="desc"?"asc":"desc")
                setChanged(true)
              }}
              style={{
                rotate:`${order==="desc"?0:180}deg`,
                transition: "rotate 0.3s"
              }}
            >
              arrow_downward
            </span>
          </span>
          <div>|</div>
          <div>
            <motion.span 
              className={`${styles.search} material-symbols-outlined`} 
              onClick={()=>{
                apiRequest(getAlbums, setIsLoading, {keyword, sort, order})
                setChanged(false)
              }}
              style={{
                cursor:"pointer",
                color:changed?"red":"lightslategrey"
              }}
              whileHover={{scale:1.2}}
            >search</motion.span>
            <input type="text" placeholder="앨범 검색" value={keyword} onChange={(e)=>setKeyword(e.target.value)}/>
          </div>
        </div>
      </div><hr />
      
      <div className={styles.albums}>
        <Folder 
          isNew={true}
          getAlbums={()=>apiRequest(getAlbums, setIsLoading, {keyword, sort, order})}
        />
        {albums.map((album, idx)=> {
          return(
          <Folder 
            key={album.albumId}
            thumbUrls={album.thumbUrls}
            albumCnt={album.count}
            albumName={album.albumName}
            albumId={album.albumId}
            getAlbums={()=>apiRequest(getAlbums, setIsLoading, {keyword, sort, order})}
          />)
        })}
      </div>
      <Loading isLoading={isLoading}/>
    </div>
  )
} 