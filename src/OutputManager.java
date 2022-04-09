import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// class to do searches
//author Zach Jackman
public class OutputManager {

	public static void readItem(BufferedReader reader, Connection conn) {
		
		String nextLine = "";

		do {
		    // Print prompt
			System.out.println("Would you like to search for a 'Artist', 'Actor', 'Author', 'Album', 'Audiobook', 'Movie', or 'Music Track'?"
					+ " Enter 'More' for more options.");
			
		    // Try read next line
		    try {
			nextLine = reader.readLine();
		    } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		    }
		    nextLine = nextLine.toLowerCase(); // sanitzing
		    switch (nextLine) {
		    case "artist":
		    searchArtist(reader, conn);
			break;
		    case "actor":
			searchActor(reader, conn);
			break;
		    case "author":
			searchAuthor(reader, conn);
			break;
		    case "album":
			// Order items here
				searchAlbum(reader, conn);
			break;
		    case "audiobook":
			// Order items here
				searchAudiobook(reader, conn);
			break;
		    case "movie":
			// Order items here
				searchMovie(reader, conn);
			break;
		    case "music track":
			// Order items here
				searchMusictrack(reader, conn);
			break;
		    case "more":
			// Order items here
			break;
		    default:
			System.out.println("Input not recognized.");
			break;
		    }

		} while (!nextLine.equals("end"));

		// Close reader and catch exception
		try {
		    reader.close();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

		try {
		    conn.close();
		} catch (SQLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

	    }

	
	static void searchArtist(BufferedReader reader,Connection conn) {
		
		System.out.println("What is the artist libray_id?");
		
    	String sqlStatement1 = "SELECT * FROM MUSICAL_ARTIST WHERE library_id = ?;";
    	String x = "";
    	PreparedStatement ps = null;
        	try {
        		try {
					x = reader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ps = conn.prepareStatement(sqlStatement1);
				ps.setInt(1,Integer.parseInt(x));
				ResultSet myRs= ps.executeQuery(); 
				
				boolean empty = true;
				
	            while (myRs.next()) {
	            	empty = false;
	                String lib_id = myRs.getString("library_id");
	                String spot_url = myRs.getString("spotify_url");
	                String app_url = myRs.getString("applemusic_url");
	                
	                System.out.println();
	                System.out.println("library id: " + lib_id);
	                System.out.println("spotify url: " + spot_url);
	                System.out.println("apple music url: " + app_url);
	                System.out.println();
	            }
	            if(empty) {
	            	System.out.println("Artist not found");
	            }
	            
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
	static void searchActor(BufferedReader reader,Connection conn) {
		System.out.println("What is the actor libray_id?");
		
    	String sqlStatement1 = "SELECT * FROM ACTOR WHERE library_id = ?;";
    	String x = "";
    	PreparedStatement ps = null;
        	try {
        		try {
					x = reader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ps = conn.prepareStatement(sqlStatement1);
				ps.setInt(1,Integer.parseInt(x));
				ResultSet myRs= ps.executeQuery(); 
				
				boolean empty = true;
				
	            while (myRs.next()) {
	            	empty = false;
	                String lib_id = myRs.getString("library_id");
	                String imdb_url = myRs.getString("imdb_url");
	                System.out.println();
	                System.out.println("library id: " + lib_id);
	                System.out.println("imdb url: " + imdb_url);
	                System.out.println();
	            }
	            if(empty) {
	            	System.out.println("Actor not found");
	            }
	            
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	static void searchAuthor(BufferedReader reader,Connection conn) {
		
		System.out.println("What is the author libray_id?");
		
    	String sqlStatement1 = "SELECT * FROM AUTHOR WHERE library_id = ?;";
    	String x = "";
    	PreparedStatement ps = null;
        	try {
        		try {
					x = reader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ps = conn.prepareStatement(sqlStatement1);
				ps.setInt(1,Integer.parseInt(x));
				ResultSet myRs= ps.executeQuery(); 
				
				boolean empty = true;
				
	            while (myRs.next()) {
	            	empty = false;
	                String lib_id = myRs.getString("library_id");
	                String reads_url = myRs.getString("goodreads_url");
	                System.out.println();
	                System.out.println("library id: " + lib_id);
	                System.out.println("goodreads url: " + reads_url);
	                System.out.println();
	            }
	            if(empty) {
	            	System.out.println("Author not found");
	            }
	            
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	static void searchAlbum(BufferedReader reader,Connection conn) {
		System.out.println("What is the album doi_eidr?");
		
    	String sqlStatement1 = "SELECT * FROM MUSICAL_ALBUM WHERE doi_eidr = ?;";
    	String x = "";
    	PreparedStatement ps = null;
        	try {
        		try {
					x = reader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ps = conn.prepareStatement(sqlStatement1);
				ps.setString(1,x);
				ResultSet myRs= ps.executeQuery(); 
				
				boolean empty = true;
				
	            while (myRs.next()) {
	            	empty = false;
	                String doi_eidr = myRs.getString("doi_eidr");
	                String library_id = myRs.getString("library_id");
	                String runtime = myRs.getString("runtime");
	                String genre = myRs.getString("genre");
	                String record_label = myRs.getString("record_label");
	                
	                System.out.println();
	                System.out.println("doi_eidr: " + doi_eidr);
	                System.out.println("library_id: " + library_id);
	                System.out.println("runtime: " + runtime);
	                System.out.println("genre: " + genre);
	                System.out.println("record label: " + record_label);
	                System.out.println();
	            }
	            if(empty) {
	            	System.out.println("Album not found");
	            }
	            
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	static void searchAudiobook(BufferedReader reader,Connection conn) {
		System.out.println("What is the audiobook doi_eidr?");
		
    	String sqlStatement1 = "SELECT * FROM AUDIOBOOK WHERE doi_eidr = ?;";
    	String x = "";
    	PreparedStatement ps = null;
        	try {
        		try {
					x = reader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ps = conn.prepareStatement(sqlStatement1);
				ps.setString(1,x);
				ResultSet myRs= ps.executeQuery(); 
				
				boolean empty = true;
				
	            while (myRs.next()) {
	            	empty = false;
	                String doi_eidr = myRs.getString("doi_eidr");
	                String length = myRs.getString("length");
	                String genre = myRs.getString("genre");
	                String author_id = myRs.getString("author_library_id");
	                String narr_id = myRs.getString("narrator_library_id");
	                
	                System.out.println();
	                System.out.println("doi_eidr: " + doi_eidr);
	                System.out.println("length: " + length);
	                System.out.println("genre: " + genre);
	                System.out.println("author_library_id: " + author_id);
	                System.out.println("narrator_library_id: " + narr_id);
	                
	                System.out.println();
	            }
	            if(empty) {
	            	System.out.println("Audiobook not found");
	            }
	            
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	static void searchMovie(BufferedReader reader,Connection conn) {
		
		System.out.println("What is the movie doi_eidr?");
		
    	String sqlStatement1 = "SELECT * FROM MOVIE WHERE doi_eidr = ?;";
    	String x = "";
    	PreparedStatement ps = null;
        	try {
        		try {
					x = reader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ps = conn.prepareStatement(sqlStatement1);
				ps.setString(1,x);
				ResultSet myRs= ps.executeQuery(); 
				
				boolean empty = true;
				
	            while (myRs.next()) {
	            	empty = false;
	                String doi_eidr = myRs.getString("doi_eidr");
	                String runtime = myRs.getString("runtime");
	                String genre = myRs.getString("genre");
	                String publisher = myRs.getString("publisher");
	                String imdb_rating = myRs.getString("imdb_rating");
	                
	                System.out.println();
	                System.out.println("doi_eidr: " + doi_eidr);
	                System.out.println("runtime: " + runtime);
	                System.out.println("genre: " + genre);
	                System.out.println("publisher: " + publisher);
	                System.out.println("imdb_rating: " + imdb_rating);
	                System.out.println();
	            }
	            if(empty) {
	            	System.out.println("Movie not found");
	            }
	            
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	static void searchMusictrack(BufferedReader reader,Connection conn) {
	
		System.out.println("What is the musictrack doi_eidr?");
		
    	String sqlStatement1 = "SELECT * FROM SONG WHERE doi_eidr = ?;";
    	String x = "";
    	PreparedStatement ps = null;
        	try {
        		try {
					x = reader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ps = conn.prepareStatement(sqlStatement1);
				ps.setString(1,x);
				ResultSet myRs= ps.executeQuery(); 
				
				boolean empty = true;
				
	            while (myRs.next()) {
	            	empty = false;
	                String doi_eidr = myRs.getString("doi_eidr");
	                String runtime = myRs.getString("runtime");
	                String name = myRs.getString("name");
	                
	                System.out.println();
	                System.out.println("doi_eidr: " + doi_eidr);
	                System.out.println("runtime: " + runtime);
	                System.out.println("name: " + name);
	                System.out.println();
	            }
	            if(empty) {
	            	System.out.println("Musictrack not found");
	            }
	            
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	}
