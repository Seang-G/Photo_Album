import { useEffect, useState } from "react"
import { motion, useAnimationControls } from "framer-motion";

import style from "./styles/logo.module.css"


export default function Logo() {
  const [x, setX] = useState(50);
  const [y, setY] = useState(50);
  const [isMouseHover, setIsMouseHover] = useState(false);

  const controls = useAnimationControls();

  const onMouseMoveHandler = (e) => {
    // setX(e.nativeEvent.y)
    // setY(e.nativeEvent.x)
    setIsMouseHover(true)
    // console.log(e.nativeEvent.x, e.nativeEvent.y)
  }

  const onMouseLeaveHandler = () => {
    setX(50)
    setY(50)
    setIsMouseHover(false)
  }

  useEffect(()=>{
    if (isMouseHover) {
      controls.start({
        border:"3px solid rgb(166, 105, 86)",
        scale:1.3
      })
    } else {
      controls.start({
        border:"3px solid rgb(194, 141, 116)",
        scale:1
      })
    }
  }, [controls, isMouseHover])

  return (
    <div
      className={style.logo} 
      onMouseMove={onMouseMoveHandler}
      onMouseLeave={onMouseLeaveHandler}
      style={{
        transform: `rotateX(${50-x}deg) rotateY(${50-y}deg)`
      }}
    >
      <motion.div className={style.diamond}
        animate={controls}/>

      <motion.div className={style.diamond}
        animate={controls}/>

      <motion.div className={style.diamond}
        animate={controls}/>
    </div>
  )
}