package com.inno72.job.core.util;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicJarClassLoader extends URLClassLoader {
   private static boolean canCloseJar = false;
   private List<JarURLConnection> cachedJarFiles;
   private static final Logger logger = LoggerFactory.getLogger(DynamicJarClassLoader.class);

   static {
      try {
         URLClassLoader.class.getMethod("close");
         canCloseJar = true;
      } catch (NoSuchMethodException e) {
      } catch (SecurityException e) {
      }
   }

   public DynamicJarClassLoader(URL[] urls, ClassLoader parent) {
      super(new URL[] {}, parent);
      init(urls);
   }
   
   
   public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
	   return super.loadClass(name, resolve);
   }
   

   public DynamicJarClassLoader(URL[] urls) {
      super(new URL[] {});
      init(urls);
   }

   public DynamicJarClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
      super(new URL[] {}, parent, factory);
      init(urls);
   }

   private void init(URL[] urls) {
      cachedJarFiles = canCloseJar ? null : new ArrayList<JarURLConnection>();
      if (urls != null) {
         for (URL url : urls) {
            this.addURL(url);
         }
      }
   }

   @Override
   protected void addURL(URL url) {
      if (!canCloseJar) {
         try {
            URLConnection uc = url.openConnection();
            if (uc instanceof JarURLConnection) {
               uc.setUseCaches(true);
               ((JarURLConnection) uc).getManifest();
               cachedJarFiles.add((JarURLConnection) uc);
            }
         } catch (Exception e) {
         }
      }
      super.addURL(url);
   }

   public void close() throws IOException {
      if (canCloseJar) {
    	  try {
    		  super.close();
    	  }catch(IOException e) {
    		  logger.error(e.getMessage(), e);
    	  }
      } else {
         for (JarURLConnection conn : cachedJarFiles) {
            conn.getJarFile().close();
         }
         cachedJarFiles.clear();
      }
   }
}
