import { useState } from "react";
import styles from "./styles/inputBox.module.css"
import { AnimatePresence, motion } from "framer-motion";

export default function InputBox({isOn, setIsOn, title, buttonSubject, onClick}){
  const [value, setValue] = useState("")

  return(
    <AnimatePresence>
    {isOn&&
    <div className={styles.changingCon}>
      <motion.div 
        className={styles.changingCover} 
        onClick={()=>{
          setIsOn(false)
          setValue("")
        }}
        transition={{duration: 0.5}}
        initial={{ 
          backgroundColor: "rgba(0, 0, 0, 0)",
        }}
        animate={{
          backgroundColor: "rgba(0, 0, 0, 0.3)",
        }}
        exit={{
          backgroundColor: "rgba(0, 0, 0, 0)",
        }}
      />
      <motion.div 
        className={styles.creating} 
        transition={{
          type: "spring",
          duration: 1
        }}
        initial={{ 
          scale: 0,
        }}
        animate={{
          scale: 1,
        }}
        exit={{
          scale: 0,
        }}
      >
        <h2>{title}</h2>
        <img src={`${process.env.PUBLIC_URL}/New_Album.png`} alt="new album"/>
        <input 
          value={value} 
          onChange={(e)=>{setValue(e.target.value)}} 
          onKeyDown={(e)=>{if(e.key==="Enter") onClick(value)}}
        />
        <button 
          onClick={()=>{
            onClick(value)
            setValue("")
          }}
        >{buttonSubject}</button>
      </motion.div>
    </div>}
    </AnimatePresence>
  );
}