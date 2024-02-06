import { useEffect, useState } from "react"
import { useLocation, useNavigate } from "react-router-dom"
import styles from "./styles/error.module.css"

export default function Error() {
  const [error, setError] = useState({status:999, message:"정상적인 에러페이지 입니다."})
  
  const location = useLocation()
  const navigate = useNavigate()
  
  useEffect(()=>{
    if(location.state) {
      setError(location.state.error);
    }
  }, [location])
  
  return <div className={styles.error}>
    <div className={styles.frame}>
      <h1 className={styles.status}>{error.status}</h1>
      <h3 className={styles.message}>{error.message}</h3>
      <div 
        className={styles.back}
        onClick={()=> {
          navigate("/")
        }}
      >메인으로 돌아가기</div>
    </div>
  </div>
}