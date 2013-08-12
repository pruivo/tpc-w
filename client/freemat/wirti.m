function [h, leg] = wirti(dat, i)
%------------------------------------------------------------------------
% function [] = wirti(dat, i)
%
% Plots histograms of web interaction response time (WIRT) for some
%  interactions.  See TPC-W Spec Clause 5.6.1.
%
% i is a vector of interaction numbers to plot.
%
%  Note that wi_init is automatically included with wi_home.
%------------------------------------------------------------------------

% Plot symbols and colors.
sym = {'k+-', 'b.-', 'ro-', 'gs-', 'm^-', 'cx-', 'kd-'};

% Find end of data.

maxC = wirtcon(i(1));

for j=2:length(i)
  maxC = max(maxC, wirtcon(i(j)));
end
% maxC = maxC * 4;
maxC = 30;
millsperi = maxC*10;

cla;
% clf;

hold on;

hx = (0:100)/100*maxC;

leg = {};
h = [];
for j=1:length(i)
  hy = zeros(1, 101);
  wh = dat.wirt{i(j)}.h;
  if (i(j)==wi_home) 
    wh(:,2) = wh(:,2) + dat.wirt{wi_init}.h(:,2);
  end
  for k=1:length(wh(:,2))
    b = min(floor(wh(k,1)/millsperi) + 1, 101);
	 hy(b)=hy(b) + wh(k,2);    
  end
 
  t = 0;
  tot = sum(hy);
  hy = hy/tot*100;
  for k=1:101
    t = t + hy(k);
    hy(k) = 100-t;
  end
  l = plot(hx, hy, sym{j});
  leg = {leg{:}, iname(i(j))};
  h = [h, l];
end

title('Response Time');
ylabel('% Interactions');
xlabel('Time (s)');

%original%legend(h, char(leg),1);

%legend;

%leg
pos1 = char(leg(1));
pos2 = char(leg(2));
pos3 = char(leg(3));
pos4 = char(leg(4));
pos5 = char(leg(5));
pos6 = char(leg(6));
pos7 = char(leg(7));

legend(pos1, pos2, pos3, pos4, pos5, pos6, pos7);

%lista;
%legend("-12.519  -12.519  -12.519  -12.519  -12.519  -12.519  -12.519", char(leg),1);
%legend('spline','pchip', 'other', 1);

ax = axis;
ax(1:2) = ceil([0, maxC]);

axis([0 20 0 100]);
