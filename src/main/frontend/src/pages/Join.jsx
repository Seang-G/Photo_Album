import { NavLink, useNavigate } from "react-router-dom";
import Logo from "../components/Logo";
import JoinAlert from "../components/JoinAlert"
import style from "./styles/login.module.css"
import { useEffect, useState } from "react";
import axios from "axios";
import Loading from "../components/Loading";

const invalidMessages = [
  "",
  "비밀번호는 8~30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.",
  "비밀번호가 일치하지 않습니다.",
  "이미 존재하는 이메일입니다.",
  "이메일을 입력해주세요.",
  "닉네임이 너무 짧습니다.",
  "옳바르지 않은 이메일 형식입니다.",
  "닉네임을 입력해주세요.",
  "비밀번호를 입력해주세요."
]

export default function Join() {
  const [nickname, setNickname] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [checkedPassword, setCheckedPassword] = useState("");
  const [passwordVis, setPasswordVis] = useState(false);
  const [checkedPasswordVis, setCheckedPasswordVis] = useState(false);
  const [invalid, setInvalid] = useState(0);
  const [invalidMessage, setInvalidMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);


  const navigate = useNavigate();

  const join = async() => {
    setIsLoading(true)
    try {
      await axios.post("/member/join", 
      {
        username:email, nickname, password, checkedPassword
      })
      alert("회원가입 완료")
      navigate("/login");
    } catch (error) {
      setInvalidMessage(error.response.data)
    }
    setIsLoading(false)
  }

  useEffect(()=>{
    const idx = invalidMessages.findIndex(v => v===invalidMessage)

    if(idx === -1) console.log("Not Expected Error");
    else if (idx !== 0) setInvalid(idx)

  }, [invalidMessage])

  return (
    <div className={style.login}>
      <div className={style.titleCon}>
        <Logo />
        <h1 className={style.title}>Photo Album</h1>
      </div>
      <input 
        type="text" 
        value={nickname} 
        placeholder="닉네임" 
        onChange={e=>setNickname(e.target.value)}
      />
      <JoinAlert 
        invalid={invalid}
        invalidCode={5}
        invalidMessage={invalidMessages[5]}
      />
      <JoinAlert 
        invalid={invalid}
        invalidCode={7}
        invalidMessage={invalidMessages[7]}
      />
      <input 
       type="text" 
       value={email} 
       placeholder="이메일" 
       onChange={e=>setEmail(e.target.value)}
      />
      <JoinAlert 
        invalid={invalid}
        invalidCode={3}
        invalidMessage={invalidMessages[3]}
      />
      <JoinAlert 
        invalid={invalid}
        invalidCode={4}
        invalidMessage={invalidMessages[4]}
      />
      <JoinAlert 
        invalid={invalid}
        invalidCode={6}
        invalidMessage={invalidMessages[6]}
      />
      
      <input 
        type={passwordVis?"text":"password"} 
        value={password} 
        placeholder="비밀번호 입력" 
        onChange={e=>setPassword(e.target.value)}
      />
      <span 
        className={`${style.eye} material-symbols-outlined`}
        onClick={()=>setPasswordVis(pre=>!pre)}
      >
        {passwordVis?"visibility":"visibility_off"}
      </span>
      <JoinAlert 
        invalid={invalid}
        invalidCode={1}
        invalidMessage={invalidMessages[1]}
      />
      <JoinAlert 
        invalid={invalid}
        invalidCode={8}
        invalidMessage={invalidMessages[8]}
      />
      <input 
        type={checkedPasswordVis?"text":"password"} 
        value={checkedPassword} 
        placeholder="비밀번호 재입력" 
        onChange={e=>setCheckedPassword(e.target.value)}
      />
      <span 
        className={`${style.eye} material-symbols-outlined`}
        onClick={()=>setCheckedPasswordVis(pre=>!pre)}
      >
        {checkedPasswordVis?"visibility":"visibility_off"}
      </span>
      <JoinAlert 
        invalid={invalid}
        invalidCode={2}
        invalidMessage={invalidMessages[2]}
      />
      <button className={style.loginButton} onClick={join}>회원가입</button>
      <NavLink to={"/login"}>취소</NavLink>
      <Loading isLoading={isLoading}/>
    </div>
  )
}