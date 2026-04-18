import Drawer from "@mui/material/Drawer"
import Toolbar from "@mui/material/Toolbar"
import List from "@mui/material/List"
import ListItemButton from "@mui/material/ListItemButton"
import WorkspacesIcon from "./assets/EmployeesIcon.svg?react"
import OfficeIcon from "./assets/OfficeIcon.svg?react"
import SearchIcon from "./assets/SearchIcon.svg?react"
import ImortExportIcon from "./assets/ImportExportIcon.svg?react"
import { useNavigate } from "react-router-dom"
import { drawerWidth } from "@features/OfficesBar/config/config"

const iconSize = drawerWidth / 2

function NavBar({ onToggleOffices }: { onToggleOffices: () => void }) {
  const nav = useNavigate()
  return (
    <Drawer
      variant="permanent"
      anchor="left"
      sx={{
        width: drawerWidth,
        flexShrink: 0,
        "& .MuiDrawer-paper": {
          width: drawerWidth,
          boxSizing: "border-box",
          display: "flex",
          flexDirection: "column",
        },
      }}
    >
      <Toolbar />
      <List>
        <ListItemButton sx={{ mb: "10px" }}>
          <SearchIcon width={iconSize} height={iconSize} />
        </ListItemButton>
        <ListItemButton onClick={onToggleOffices}>
          <OfficeIcon width={iconSize} height={iconSize} />
        </ListItemButton>
      </List>
      <List sx={{ mt: "auto", mb: "20px" }}>
        <ListItemButton sx={{ mb: "10px" }}onClick={()=> nav('/adminpanel')}>
          <WorkspacesIcon width={iconSize} height={iconSize} />
        </ListItemButton>
        <ListItemButton >
          <ImortExportIcon width={iconSize} height={iconSize} />
        </ListItemButton>
      </List>
    </Drawer>
  )
}

export default NavBar
