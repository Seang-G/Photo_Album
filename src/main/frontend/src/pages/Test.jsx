import axios from "axios"

export default function Test() {

  const test = async() => {
    try{
      const res = await axios.get("/albums", {
        headers:{
          "Authorization": `Bearer ${sessionStorage.getItem("AccessToken")}`
        }
      });
    } catch(e) {
      console.dir(e)
    }
  }

  const sayHello = async() => {
    try{
      const res = await axios.get("/hello");
      console.log(res.data)
    } catch(e) {
      console.dir(e)
    }
  }


  
  return (
    <div>
      <button onClick={test}>요청</button>
      <button onClick={sayHello}>Hello</button>
    </div>
  )
}