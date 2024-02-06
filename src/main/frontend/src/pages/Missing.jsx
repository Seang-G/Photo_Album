import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function Missing() {
  const navigate = useNavigate()

  useEffect(()=>navigate("/error", {state:{error:{status:"404", message:"페이지를 찾을 수 없습니다."}}}),[])
}