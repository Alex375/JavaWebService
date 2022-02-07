package fr.epita.utils;

import fr.epita.domain.entity.Pos;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Map
{
    public static String readMap(String path)
    {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path)))
        {
            String s = br.readLine();
            while (s != null)
            {
                sb.append(s);
                s = br.readLine();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println(sb.toString());
        return decodeRle(sb.toString());
    }

    public static String decodeRle(String src)
    {
        StringBuilder sb = new StringBuilder();


        int i = 0;
        int number;
        while (i < src.length()) {
            number = 0;
            while ((i < src.length()) && Character.isDigit(src.charAt(i))) {
                number = number * 10 + src.charAt(i++) - '0';
            }
            StringBuilder s = new StringBuilder();
            while ((i < src.length()) && !Character.isDigit(src.charAt(i))) {
                s.append(src.charAt(i));
            }

            if (number > 0) {
                sb.append(String.valueOf(s).repeat(number));
            }
            else {
                sb.append(s);
            }
        }

        return sb.toString();
    }

    public static List<String> formatMap(String map)
    {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < 15; i++)
        {
            res.add(encodeRle(map.substring(i * 17, (i * 17) + 17)));
        }
        return res;
    }
    public static List<String> formatMapNoRLE(String map)
    {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < 15; i++)
        {
            res.add(map.substring(i * 17, (i * 17) + 17));
        }
        return res;
    }

    public static String deformatMap(List<String> map)
    {
        StringBuilder sb = new StringBuilder();
        map.forEach(sb::append);
        return sb.toString();
    }

    public static String putMap(String map, Pos pos, char c)
    {
        List<String> mapS = formatMapNoRLE(map);
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < mapS.size(); y++)
        {
            if (y == pos.posY)
            {
                for (int x = 0; x < 17; x++)
                {
                    if (x == pos.posX)
                        sb.append(c);
                    else
                        sb.append(mapS.get(y).charAt(x));
                }
            }
            else
            {
                sb.append(mapS.get(y));
            }
        }
        return sb.toString();
    }

    public static String encodeRle(String src)
    {
        StringBuilder res = new StringBuilder();
        int count;
        for (int i = 0; i < src.length(); i++)
        {
            count = 1;
            while (count < 9 && i < src.length() - 1 && src.charAt(i) == src.charAt(i + 1))
            {
                count++;
                i++;
            }
            res.append(count);
            res.append(src.charAt(i));
        }
        return res.toString();
    }
}
