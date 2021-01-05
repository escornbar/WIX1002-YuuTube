package yuu.tube;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Video {
    private String vidTitle, filename;
    private int vidViews, vidLikes, vidDislikes;
    private String[] vidComments;
    private String fileDestination = "C:\\Users\\syaam\\Videos\\";
    
    public Video() {
        this.vidTitle = null;
        this.vidViews = 0;
        this.vidLikes = 0;
        this.vidDislikes = 0;
        this.vidComments = null;
    }
    
    public void searchVid(){
        Scanner s=new Scanner(System.in);
        vidTitle=s.nextLine();
        MyConnection connection=new MyConnection();
        Connection conn = null; 
        PreparedStatement st = null; 
        ResultSet rs = null;
        conn = connection.getConnection();
        try{
            String SQL="SELECT * FROM public.videostats WHERE title=?";
            st = conn.prepareStatement(SQL); 
            st.setString(1, vidTitle);
            rs = st.executeQuery();
            if  (rs.next()) { 
                String title = rs.getString("title");
                int views = rs.getInt("views");
                int likes = rs.getInt("likes");
                int dislikes = rs.getInt("dislikes");
                System.out.println("Title: "+title+
                                   "\nViews: "+views+
                                   "\nLikes: "+likes+
                                   "\nDislikes: "+dislikes);
            } 
        } catch (SQLException ex) {
            Logger.getLogger(RegisterForm.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    //Method to upload a video
    public void uploadVideo(){
        File f = new File("E:\\Video Files\\");
        if(!f.exists()){
            f.mkdir();
        }
            
        final int BUFFERSIZE = 4 * 1024;
        Scanner s = new Scanner(System.in);
        System.out.println("\n--VIDEO UPLOAD GUIDE--");
        System.out.println("Enter the file path of the video to be uploaded: C:/My_Folder/Videos");
        System.out.println("Enter the name of the video: test.mp4");
        System.out.println("\n--YUUTUBE VIDEO UPLOAD--");
        System.out.print("Enter the file path of the folder the video is in: ");
        String sourceFilePath = s.nextLine();
        System.out.print("Enter the file name: ");
        String outputFilePath = s.nextLine();
        System.out.print("Enter video title: ");
        vidTitle=s.nextLine();
        
        try(
            FileInputStream fin = new FileInputStream(new File(sourceFilePath + "/" + outputFilePath));
            FileOutputStream fout = new FileOutputStream(new File(fileDestination + outputFilePath));
            ){
            byte[] buffer = new byte[BUFFERSIZE];
            System.out.println("Upload completed");
            while(fin.available() != 0) {
                fin.read(buffer);
                fout.write(buffer);
            } 
        } catch(Exception e) {
            System.out.println("Something went wrong! Reason: " + e.getMessage());
            }
        
        PreparedStatement st;
        int rowsAffected=0;
        String SQL="INSERT INTO videostats(title, likes, dislikes, views, filename)"+"values(?,?,?,?,?)";
        try{
            st=MyConnection.getConnection().prepareStatement(SQL);
            st.setString(1, vidTitle);
            st.setInt(2, vidLikes);
            st.setInt(3, vidDislikes);
            st.setInt(4, vidViews);
            st.setString(5, outputFilePath);
            rowsAffected=st.executeUpdate();
            } catch (SQLException ex) {
            System.out.println("Error in uploading to database");
            }
        } 
    
    //Method to play video
    public void playVideo(){
        vidViews++;
        int rowsAffected=0;
        MyConnection connection=new MyConnection();
        Connection conn = null; 
        PreparedStatement st = null; 
        ResultSet rs = null;
        conn = connection.getConnection();
        try{
            String SQL="UPDATE videostats "+"SET views = ?"+"WHERE title = ?";
            st=MyConnection.getConnection().prepareStatement(SQL);
            st.setInt(1, vidViews);
            st.setString(2, vidTitle);
            rowsAffected = st.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error updating to database");
            }
        vidViews--;
        try{
            String SQL="SELECT * FROM public.videostats WHERE title=?";
            st = conn.prepareStatement(SQL);
            st.setString(1, vidTitle);
            rs = st.executeQuery();
            if(rs.next()){
                filename=rs.getString("filename");
            }
            File f = new File(fileDestination+filename);
            Desktop.getDesktop().open(f);
        } catch(IOException e1){
        } catch (SQLException ex) {
          Logger.getLogger(RegisterForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Method to delete video
    public void deleteVideo(){
        Scanner s =new Scanner(System.in);
        System.out.print("Enter name of video: ");
        String video = s.nextLine();
        PreparedStatement st;
        String SQL="DELETE FROM public.videostats WHERE filename = ?";
        int rowsAffected=0;
        try{
            st=MyConnection.getConnection().prepareStatement(SQL);
            st.setString(1, video);
            rowsAffected = st.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error deleting in database");
        }
        File f = new File(fileDestination + video); 
        if (f.delete()) { 
            System.out.println("Video Deleted: " + f.getName());
        } else {
            System.out.println("Failed to delete the video");
        }
    }
    
    public void likeVid(){
        vidLikes++;
        int rowsAffected=0;
        MyConnection connection=new MyConnection();
        Connection conn = null; 
        PreparedStatement st = null; 
        ResultSet rs = null;
        conn = connection.getConnection();
        try{
            String SQL="UPDATE videostats "+"SET likes = ?"+"WHERE title = ?";
            st=MyConnection.getConnection().prepareStatement(SQL);
            st.setInt(1, vidLikes);
            st.setString(2, vidTitle);
            rowsAffected = st.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error updating to database");
            }
        vidLikes--;
    }
    
    public void dislikeVid(){
        vidDislikes++;
        int rowsAffected=0;
        MyConnection connection=new MyConnection();
        Connection conn = null; 
        PreparedStatement st = null; 
        ResultSet rs = null;
        conn = connection.getConnection();
        try{
            String SQL="UPDATE videostats "+"SET dislikes = ?"+"WHERE title = ?";
            st=MyConnection.getConnection().prepareStatement(SQL);
            st.setInt(1, vidDislikes);
            st.setString(2, vidTitle);
            rowsAffected = st.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error updating to database");
            }
        vidDislikes--;
    }
    
    public void showVidStats(){
        
    }
}
