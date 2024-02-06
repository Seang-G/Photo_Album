import axios from "axios"
import { useNavigate } from "react-router-dom"

export default async function apiRequest (request, setIsLoading=function(){}, params={}) {

  const getAccessToken = async() => {

    const res = await axios.get("/member/refresh", {
      params: {refreshToken:sessionStorage.getItem("RefreshToken")}
    })
    
    sessionStorage.setItem("AccessToken", res.data.accessToken)
    sessionStorage.setItem("tokenExpiresIn", res.data.tokenExpiresIn + new Date().getTime())
  }

  if(sessionStorage.getItem("AccessToken")){
    setIsLoading(true)

    try {
      if(sessionStorage.getItem("tokenExpiresIn")) {
        const now = new Date().getTime()
        if(now > Number(sessionStorage.getItem("tokenExpiresIn"))) {
          await getAccessToken()
        }
      }
        await request(params)
    }
    catch(err) {
      sessionStorage.clear()
      alert('세션이 만료되었습니다.')
      window.location.href = "/"
    }
    
    setIsLoading(false)
  } else {
    sessionStorage.clear()
  }

}