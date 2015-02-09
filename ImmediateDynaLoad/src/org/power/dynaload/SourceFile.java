package org.power.dynaload;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.power.dynaload.compile.DyncSourceCompiler;
import org.power.dynaload.proxy.DyncServProxy;
import org.power.dynaload.registry.ServiceRegistry;
import org.power.dynaload.utils.IOUtils;

/**
 * ����һ��Դ�ļ�.
 * @author li.zhang
 * 2014-9-15
 */
public class SourceFile
{
    /** Դ�ļ�. **/
    private final File sourceFile;
    
    /** ���dyncode�ļ��е�·��. **/
    private final String relativePath;
    
    /** Դ�ļ���Ӧ�����ȫ�޶���. **/
    private String fullClassName;
    
    /**
     * ���췽��
     * @param file
     */
    public SourceFile(File file)
    {
        sourceFile = file;
        relativePath = IOUtils.getRelativePath(file, new File("dyncode"));
        
        fullClassName = relativePath + "\\" + file.getName().substring(0, file.getName().lastIndexOf(".java"));
        fullClassName = fullClassName.replaceAll("\\\\", ".");
    }

    public File getSourceFile()
    {
        return sourceFile;
    }

    public String getRelativePath()
    {
        return relativePath;
    }
    
    /**
     * ����ǰԴ�ļ������䶯ʱ�������������.
     */
    public void notifyChanged()
    {
        System.out.println("Source File " + sourceFile.getName() + "changed....");
        DyncSourceCompiler compiler = new DyncSourceCompiler();
        compiler.compile(sourceFile);
        
        //���ö�Ӧ������changed��־λΪtrue.
        ServiceRegistry registry = ServiceRegistry.getInstance();
        Map<String, String> dyncServiceNames = registry.getDyncServiceNames();
        if (null == dyncServiceNames)
        {
            return;
        }
        
        String serviceName = dyncServiceNames.get(fullClassName);
        IDynamicService dyncServProxy = registry.queryService(serviceName);
        if (null == dyncServProxy)
        {
            return;
        }
        
        DyncServProxy proxy = (DyncServProxy)Proxy.getInvocationHandler(dyncServProxy);
        proxy.notifyChanged();
        
    }
}