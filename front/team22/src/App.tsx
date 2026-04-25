import { BrowserRouter, Route, Routes } from "react-router-dom"
import HomePage from "@pages/HomePage"
import OfficeMapEditor from "@pages/OfficeMapEditor"
import AddFloor from "@pages/AddFloor"
import AuthPage from "@pages/AuthPage"
import RegisterPage from "@pages/RegisterPage"
import AdminPanelPage from "@pages/AdminPanelPage"
import { ProtectedRoute } from "@shared/components/ProtectedRoute"
import { initAuth } from "@shared/store/auth"
import { useEffect } from "react"
import { useUnit } from "effector-react"
import { ErrorSnackbar } from "@shared/ui/ErrorSnackbar"

function App() {
  const startAuth = useUnit(initAuth)

  useEffect(() => {
    startAuth() // вызов эффекта при монтировании компонента
  }, [])

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/auth" element={<AuthPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <HomePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/office/:officeId/floor/:floorId"
          element={
            <ProtectedRoute>
              <OfficeMapEditor />
            </ProtectedRoute>
          }
        />
        <Route
          path="/office/:officeid/createfloor"
          element={
            <ProtectedRoute>
              <AddFloor />
            </ProtectedRoute>
          }
        />
        <Route
          path="/adminpanel"
          element={
            <ProtectedRoute>
              <AdminPanelPage />
            </ProtectedRoute>
          }
        />
      </Routes>
      <ErrorSnackbar />
    </BrowserRouter>
  )
}

export default App
