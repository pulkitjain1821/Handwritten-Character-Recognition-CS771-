function[bound] = findBoundary(img)
rows = size(img,1);
cols = size(img,2);

rows
cols

found = 0;
for i = 1:rows
    if(found ==0)
        for j = 1:cols
            if(img(i,j)==0)
                startRow = i;
                found = 1;
                break;
            end
        end
    end
end

startRow

found = 0;
for i = 1:rows
    if(found ==0)
        for j = 1:cols
            if(img(rows-i,j)==0)
                endRow = rows-i;
                found = 1;
                break;
            end
        end
    end
end
endRow


found = 0;
for j =1:cols
    if(found==0)
        for i =1:rows
            if(img(i,j)==0)
                startCol = j;
                found = 1;
                break
            end
        end
    end
end
startCol

found = 0;
for j =1:cols
    if(found==0)
        for i =1:rows
            if(img(i,cols-j)==0)
                endCol = cols-j;
                found = 1;
                break
            end
        end
    end
end
endCol

height = endRow - startRow
length = endCol - startCol

x_center = floor((startCol + endCol)/2);
y_center = floor((startRow + endRow)/2);

if (height > length)
    startRow = y_center - floor(height/2);
    startCol = x_center - floor(height/2);
    endRow   = y_center + floor(height/2);
    endCol   = x_center + floor(height/2);
    bound = imresize(img(startRow-4:endRow+4,startCol-4:endCol+4),[50 50]);
else
    height = length;
    startRow = y_center - floor(height/2);
    startCol = x_center - floor(height/2);
    endRow   = y_center + floor(height/2);
    endCol   = x_center + floor(height/2);
    bound = imresize(img(startRow-4:endRow+4,startCol-4:endCol+4),[50 50]);
end
