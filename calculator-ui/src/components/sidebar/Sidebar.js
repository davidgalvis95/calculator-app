import React, { useState } from "react";
import { styled } from "@mui/system";
import {
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
} from "@mui/material";
import { Link } from "react-router-dom";
import {
  Calculate as CalculateIcon,
  AccountCircle as AccountCircleIcon,
  History as HistoryIcon,
  Logout as LogoutIcon,
  Group as GroupIcon,
} from "@mui/icons-material";
import { useNavigate } from "react-router-dom";

const SidebarWrapper = styled(Drawer)(({ theme }) => ({
  width: "auto",
  flexShrink: 0,
  "& .MuiDrawer-paper": {
    width: "auto",
    backgroundColor: "#0a3d62",
    display: "flex",
    flexDirection: "column",
    justifyContent: "space-between",
    alignItems: "center",
  },
}));

const SidebarContainer = styled("div")(({ theme }) => ({
  overflow: "auto",
  alignItems: "center",
  padding: theme.spacing(2),
}));

const SignOutButtonWrapper = styled("div")(({ theme }) => ({
  padding: theme.spacing(2),
}));


const sideBarIconsStyles = {color: "#82ccdd", fontSize: "35px", paddingTop: "10px"}

const Sidebar = (props) => {

  const [sideBarState, setSideBarState] = useState(false);
  const navigate = useNavigate();

  const SignOutButton = styled(ListItem)(({ theme }) => ({
    backgroundColor: "#82ccdd",
    color: "#0a3d62",
    width: props.open ? "auto" : "60px",
    "&:hover": {
      backgroundColor: "#60a3bc",
      color: "black",
      "& .MuiListItemIcon-root": {
        color: "black",
      },
    },
  }));

  const navigateProfile = () => {
    navigate("/profile")
  }

  const navigateCalculator = () => {
    navigate("/calculator")
  }

  const navigateHistory = () => {
    navigate("/history")
  }

  const navigateUsers = () => {
    navigate("/users")
  }

  return (
    <SidebarWrapper variant="permanent">
      <SidebarContainer>
        <List>
          <div title="Profile">              
            <AccountCircleIcon sx={sideBarIconsStyles} onClick={navigateProfile}/>
          </div>
          <div title="Calculator">              
            <CalculateIcon sx={sideBarIconsStyles} onClick={navigateCalculator}/>
          </div>
          <div title="History">              
            <HistoryIcon sx={sideBarIconsStyles} onClick={navigateHistory}/>
          </div>
          <div title="Users">              
            <GroupIcon sx={sideBarIconsStyles} onClick={navigateUsers}/>
          </div>
        </List>
      </SidebarContainer>
      <div title="Log out">
      <SignOutButtonWrapper>
        <List>
          <SignOutButton
            button
            component={Link}
            to="/login"
            style={{ borderRadius: "4px" }}
          >
            <ListItemIcon sx={{ color: "#0a3d62" }}>
              <LogoutIcon />
            </ListItemIcon>
            {props.open && <ListItemText primary="Sign Out" />}
          </SignOutButton>
        </List>
      </SignOutButtonWrapper>
      </div>

    </SidebarWrapper>
  );
};

export default Sidebar;
