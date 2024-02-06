import { AnimatePresence, motion } from "framer-motion";
import style from "./styles/joinAlert.module.css"

const invalidVisStyle = {
  hidden: {opacity: 0},
  visible: {
    opacity: 1,
  }
}

export default function JoinAlert({invalid, invalidCode, invalidMessage}) {
  return (
    <AnimatePresence>
      {invalid===invalidCode&&(
        <motion.div 
          className={style.invalidCover}
          variants={invalidVisStyle}
          initial="hidden"
          exit="hidden"
          animate="visible"
        >
          <div className={style.invalid}>
            {invalidMessage}
          </div>
        </motion.div>
      )}
      </AnimatePresence>
  )
}