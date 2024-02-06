import { useEffect, useRef, useState } from "react"
import styles from "./styles/photo.module.css"
import { AnimatePresence, motion } from "framer-motion";
import { useNavigate } from "react-router-dom";

export default function Photo({photoId, setCheckedPhotos, checkedPhotos}) {
  const [isHover, setIsHover] = useState(false);
  const [isCheckHover, setIsCheckHover] = useState(false);
  const [isSelected, setIsSelected] = useState(false);
  const navigate = useNavigate();

  const photo = useRef()

  useEffect(()=>{
    if (isSelected) {
      photo.current.parentNode.style.padding = "5px"
    } else {
      photo.current.parentNode.style.padding = "0px"
    }
    
  }, [isSelected])

  useEffect(()=>{
    let idx =  checkedPhotos.indexOf(photoId)
    if(idx === -1) setIsSelected(false)
    else setIsSelected(true)
  }, [checkedPhotos])

  return(
    <div 
      className={styles.photo}
      onMouseEnter={()=>setIsHover(true)}
      onMouseLeave={()=>setIsHover(false)}
      style={{
        width:isSelected?"235px":"245px",
      }}
      ref={photo}
    >
      <AnimatePresence>
      {(isHover||isSelected)&&<motion.div
        className={styles.hoverFunction}
        initial={{opacity:0}}
        animate={{opacity:1}}
        exit={{opacity:0}}
      >
        <div 
          className={`${styles.radio} material-symbols-outlined`}
          onMouseEnter={()=>setIsCheckHover(true)}
          onMouseLeave={()=>setIsCheckHover(false)}
          onClick={()=>{
            setCheckedPhotos(pre=>{
              let idx =  pre.indexOf(photoId)
              if(idx === -1) {
                return [...pre, photoId]
              }
              else {
                pre.splice(idx, 1)
                return [...pre]
              }
            })
          }}
          style={{
            fontVariationSettings:isSelected&&"'FILL' 1"
          }}
        >{isCheckHover||isSelected?"check_circle":"radio_button_unchecked"}
        </div>
        <div 
          className={`${styles.detail} material-symbols-outlined`}
          onClick={()=>navigate(`./${photoId}`)}
        >info
        </div>
      </motion.div>}
      </AnimatePresence>

    </div>
  )
}