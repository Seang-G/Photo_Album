import { useEffect, useState } from "react";
import Logo from "../components/Logo";
import style from "./styles/login.module.css"
import { NavLink, useNavigate } from "react-router-dom";
import axios from "axios";
import JoinAlert from "../components/JoinAlert";

import Loading from "../components/Loading";

const invalidMessages = [
  "",
  "등록되지 않은 이메일입니다.",
  "비밀번호가 일치하지 않습니다."
]

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [saveId, setSaveId] = useState(false);
  const [passwordVis, setPasswordVis] = useState(false);
  const [invalid, setInvalid] = useState(0);
  const [isLoading, setIsLoading] = useState(false);

  const navigate = useNavigate();

  const login = async() => {
    try {
      setIsLoading(true)
      const res = await axios.post("/member/login", {username:email, password})
      
      sessionStorage.setItem("AccessToken", res.data.accessToken)
      sessionStorage.setItem("RefreshToken", res.data.refreshToken)
      sessionStorage.setItem("tokenExpiresIn", res.data.tokenExpiresIn + new Date().getTime())
      if(saveId) localStorage.setItem("savedId", email)
      else localStorage.removeItem("savedId")
      navigate("/albums")
    } catch (error) {
      if(error.response.data==="등록되지 않은 이메일입니다.") {
        setEmail("")
        setInvalid(1)
        
      } else if(error.response.data==="비밀번호가 일치하지 않습니다.") {
        setPassword("")
        setInvalid(2)
      }
    }
    setIsLoading(false)
  }

  useEffect(()=>{
    if(sessionStorage.getItem("AccessToken")) navigate("./albums")
    const v = localStorage.getItem("savedId")
    if (v) {
      setEmail(v);
      setSaveId(true);
    }
  }, [])


  return(
    <div className={style.login} >
      <div className={style.titleCon}>
        <Logo />
        <h1 className={style.title}>Photo Album</h1>
      </div>

      <input 
        type="text" 
        value={email} 
        onChange={e=>setEmail(e.target.value)}
        placeholder="이메일 입력"
        onKeyDown={(e)=>{if(e.key==="Enter") login()}}
      />
      <JoinAlert 
        invalid={invalid}
        invalidCode={1}
        invalidMessage={invalidMessages[1]}
      />
      <input 
        type={passwordVis?"text":"password"}
        value={password} 
        onChange={e=>setPassword(e.target.value)}
        placeholder="비밀번호 입력"
        className={style.password}
        onKeyDown={(e)=>{if(e.key==="Enter") login()}}
      />
      <JoinAlert 
        invalid={invalid}
        invalidCode={2}
        invalidMessage={invalidMessages[2]}
      />
      <span 
        className={`${style.eye} material-symbols-outlined`}
        onClick={()=>setPasswordVis(pre=>!pre)}
      >
        {passwordVis?"visibility":"visibility_off"}
      </span>
      <div className={style.checkCon}>
        <input 
          id="saveId"
          type="checkbox"
          checked={saveId}
          onChange={e => setSaveId(e.target.checked)}
        />
        <label htmlFor="saveId">아이디 저장</label>
      </div>
      <button className={style.loginButton} onClick={login}>로그인</button>
      {/* <NavLink>비밀번호 찾기</NavLink> */}
      <NavLink to={"/join"}>회원가입</NavLink>
      <Loading isLoading={isLoading}/>
    </div>
  )
}