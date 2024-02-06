import { useState, useEffect } from "react"
import { useLocation, useNavigate } from "react-router-dom";
import { AnimatePresence, motion } from "framer-motion";
import axios from "axios";

import styles from "./styles/nav.module.css"
import Logo from "../components/Logo"
import serializeError from "../functions/serializeError";
import apiRequest from "../functions/apiRequest";

export default function Nav(){
  const [nickname, setNickname] = useState("");
  const [isAccountOn, setIsAccountOn] = useState(false);

  const location = useLocation();
  const navigate = useNavigate();
  
  const getNickname = async() => {
    if (sessionStorage.getItem("AccessToken")){
      try{
        const res = await axios.get("/member",
        {
          headers:{Authorization: `Bearer ${sessionStorage.getItem("AccessToken")}`}
        });
        setNickname(res.data)
      } catch (error) {
        const serializableError = serializeError(error);
        navigate("/error", {state:{error:serializableError}})
      }
    }
  }

  const logout = () => {
    sessionStorage.clear()
    setIsAccountOn(false)
    setNickname("")
    navigate("/")
  }

  useEffect(()=>{
    apiRequest(getNickname);
  }, [])

  return(
  (sessionStorage.getItem("AccessToken")&&location.pathname!=="/error")&&<div className={styles.nav} >
    <div onClick={()=>navigate("/albums")} className="mainLogo">
      <Logo />
      <h1
      >Photo Album</h1>
    </div>
      <motion.div 
        className={`${styles.symbol} material-symbols-outlined`}
        onClick={()=>{
          setIsAccountOn(pre=>!pre)
          if(nickname==="")apiRequest(getNickname)
        }}
      >
        account_circle
      </motion.div>
      <AnimatePresence>
      {isAccountOn&&<motion.div 
        className={styles.navAccount} 
        initial={{opacity:0}}
        animate={{opacity:1}}
        exit={{opacity:0}}
      >
        <div>{nickname}</div>
        <hr />
        <div className={styles.logout} onClick={logout}>로그아웃</div>
      </motion.div>}
      </AnimatePresence>
    
  </div>
  )
}