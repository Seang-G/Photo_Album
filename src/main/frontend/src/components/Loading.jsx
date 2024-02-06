import { SquareLoader } from "react-spinners";
import styles from "./styles/loading.module.css"

export default function Loading({isLoading}) {
  return(isLoading&&
    <div className={styles.loading}>
      <SquareLoader color="#e89146" 
        loading={isLoading}
        cssOverride={{
          animationDelay: "0s"
        }}
      />

      <SquareLoader color="#cc662f" 
        loading={isLoading}
        cssOverride={{
          animationDelay: "0.2s"
        }}
      />

      <SquareLoader color="#ad471c" 
        loading={isLoading} 
        cssOverride={{
          animationDelay: "0.4s"
        }}
      />
    </div>
  )
}