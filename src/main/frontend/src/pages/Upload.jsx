import { useEffect, useState } from "react"
import styles from "./styles/upload.module.css"
import axios, { AxiosHeaders, toFormData } from "axios";
import { useNavigate, useParams } from "react-router-dom";
import serializeError from "../functions/serializeError";
import apiRequest from "../functions/apiRequest";
import Loading from "../components/Loading";

export default function Upload(){

  const [photos, setPhotos] = useState([]);
  const [isDraging, setIsDraging] = useState(false);
  const [formData, setFormData] = useState(new FormData());
  const [isLoading, setIsLoading] = useState(false);

  const params = useParams();
  const navigate = useNavigate();

  const readPhoto = photo => {
    const reader = new FileReader();
    reader.onload = function (e) {
      photo.result = e.target.result
      setPhotos(pre=>[...pre, photo])
    }
    reader.readAsDataURL(photo)
  }

  const onDrop = e => {
    e.preventDefault()
    e.stopPropagation()
    for (var file of e.dataTransfer.files)
      readPhoto(file);
  }

  const cancel = e => {
    setPhotos(pre=>{
      pre.splice(e.target.parentNode.id, 1)
      return [...pre]
    })
  }

  useEffect(()=>{
    if (photos){
      setFormData(pre=>{
        const form = new FormData();
        photos.forEach((photo, idx)=>{
          form.append("photos", photo)
        })
        
        return form
      })
    }
  }, [photos])

  const upload = async() => {
    try {
      await axios.post(`/albums/${params.albumId}/photos`,
        formData,
        {headers: {
          Authorization: `Bearer ${sessionStorage.getItem("AccessToken")}`,
          "Content-Type": "multipart/form-data"
        }}
      )
      navigate("./..")
    } catch (error){
      console.log(error.response)
      alert(error.response.data)
      setPhotos([])
    }
  }

  return(
    <div className={styles.upload} onDrop={(e)=>e.preventDefault()}>
      <div className={styles.back} onClick={()=>navigate("./..")}>
        <span className="material-symbols-outlined">
          arrow_back
        </span>
        {" "}앨범 목록으로 돌아가기
      </div>
      <h1>앨범에 사진을 추가해주세요</h1>
      <input id="input" type="file" accept=".png,.jpg,.jpeg" multiple 
        onChange={e=>{
          for (var file of e.target.files){
            readPhoto(file)
          }
      }} />
      
      <div 
        className={styles.inputBox}
        onDrop={(e)=>onDrop(e)}
        onDragEnter={()=>setIsDraging(true)}
        onDragOver={(e)=>{
          e.preventDefault()
          setIsDraging(true)
        }}
        onMouseOver={()=>setIsDraging(false)}
        onDragLeave={()=>setIsDraging(false)}
        style={{
          backgroundColor:isDraging?"rgb(184, 184, 184)":"rgb(211, 211, 211)",
          border: isDraging?"2px dashed black":"0px"
        }}
      >
        {photos.length === 0?
          <div className={styles.info}>
            <span className="material-symbols-outlined">cloud_upload</span><br />
            <div>파일을 여기에 드래그하세요.</div>
            <label htmlFor="input" role="button">내 컴퓨터에 추가</label>
          </div>:
          <div className={styles.thumCon}>
            {photos.map((photo, idx)=>{
              return(
                <div key={idx} className={styles.thumb} id={idx}>
                  <div className="material-symbols-outlined" onClick={(e)=>{cancel(e)}}>
                    cancel
                  </div>
                  <img src={photo.result} alt=""/>
                </div>
              )
            })}
          </div>
        }
      </div>
      <button onClick={()=>apiRequest(upload, setIsLoading)} disabled={photos.length === 0}>파일 업로드</button>
      <Loading isLoading={isLoading}/>
    </div>
  )
}