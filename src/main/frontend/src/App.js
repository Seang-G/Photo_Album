import { BrowserRouter, Route, Routes, useNavigate } from 'react-router-dom';
import Login from './pages/Login';
import Join from './pages/Join';
import Test from './pages/Test';
import AlbumList from './pages/AlbumList';
import Nav from './pages/Nav';
import Album from './pages/Album';
import Upload from './pages/Upload';
import PhotoDetail from './pages/PhotoDetail';
import Error from './pages/Error';
import styles from "./App.module.css"
import Missing from './pages/Missing';

function App() {

  return (
    <div 
      className={styles.app}
      onDragStart={(e) => e.preventDefault()}
      onContextMenu={(e) => e.preventDefault()}
    >
      <BrowserRouter>
      <Nav />
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />
        <Route path="/join" element={<Join />} />
        <Route path="/test" element={<Test />} />
        <Route path="/albums" element={<AlbumList />} />
        <Route path="/albums/:albumId" element={<Album />} />
        <Route path="/albums/:albumId/upload" element={<Upload />} />
        <Route path='/albums/:albumId/:photoId' element={<PhotoDetail />} />
        <Route path='/error' element={<Error status='404' message='페이지를 찾을 수 없습니다.' />} />
        <Route path='/*' element={<Missing />} />
      </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
