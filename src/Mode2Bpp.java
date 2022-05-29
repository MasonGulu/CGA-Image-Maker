import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Mode2Bpp implements ICGA {
    private Palette palette;
    private int width;
    private int height;
    private PaletteImage image;
    private PALETTE selectedPalette;

    enum PALETTE {
        OLD_0,
        OLD_1,
        OLD_2,
        NEW_0,
        NEW_1,
        NEW_2
    }

    private static final int[] old_0 = new int[]{0, 602520, 24835, 31080, 0, 791386, 16128, 30583, 860506, 1529841, 34655, 565189, 795218, 1718452, 812602, 957650, 4587859, 5847039, 3495526, 4425145, 4391241, 5511853, 4272961, 4621501, 5515181, 6708735, 4488130, 5352447, 5318306, 6373375, 5200540, 5548799, 2563328, 2970476, 1936896, 2403890, 2564864, 3554099, 2649344, 2665276, 3490871, 3897543, 2864179, 3265932, 3492394, 4481677, 3576845, 3527062, 0, 209035, 20501, 34650, 0, 594772, 11264, 33371, 794969, 1136357, 30320, 634292, 794452, 1521837, 611149, 501939, 11085640, 10574776, 9667652, 9871765, 11543896, 11352979, 10779147, 9871762, 12012962, 11502079, 10594973, 10799345, 12602290, 12280557, 11640677, 10733804, 7472983, 8600063, 6445932, 7175882, 8127851, 9314005, 7092293, 7244218, 8400305, 9527551, 7438791, 8103423, 8989637, 10241535, 8019616, 8171519, 5449472, 6315379, 4620800, 5218630, 5382400, 5978665, 5600512, 5681979, 6376751, 7177165, 5482559, 6146209, 6309681, 6906242, 6527753, 6609558, 10891311, 11951839, 9995845, 11970004, 10758445, 12598453, 10708508, 11975349, 11818632, 13010175, 10922911, 12963071, 11686024, 13525759, 11635832, 12968447, 3809024, 4543879, 3048448, 3513433, 3743744, 4732490, 3760896, 3840614, 4670279, 5273823, 3778380, 4178095, 4539198, 5462688, 4622119, 4701629, 8463426, 9723117, 7436375, 8366250, 6506496, 9321884, 8083503, 8431788, 9193625, 10452735, 8166572, 9030911, 8997007, 10117620, 8878727, 9227263, 6504960, 6846043, 5878272, 6345249, 3677696, 7495714, 6590720, 6606891, 7234595, 7641522, 6673697, 7075194, 7236119, 8225402, 7320320, 7336579, 3743488, 4084858, 2847748, 3582537, 15485254, 4535875, 3559680, 3515721, 4538949, 4880338, 3577436, 4378273, 4473151, 5266074, 4354872, 4311457, 15026999, 14515878, 13608753, 13813380, 11938138, 15294593, 14654720, 13813122, 15756942, 15245823, 14338954, 14543325, 16215198, 16089818, 15450450, 14543065, 11348550, 12475891, 10321499, 11051704, 11938138, 13255364, 10968117, 11119784, 12078749, 13271295, 11051442, 11781887, 12799153, 13985279, 11698059, 11849983, 9325312, 10125410, 8496640, 9094451, 9257984, 9854744, 9476352, 9557802, 10055196, 10920889, 9226796, 9824653, 9988126, 10584432, 10205952, 10287746, 14832669, 15958736, 13871155, 15846082, 14699805, 16474532, 14649612, 15982501, 15562614, 16622847, 14667147, 16575999, 15429748, 16745468, 15379555, 16646140};
    private static final int[] old_1 = new int[]{0, 0, 2521088, 22272, 4594688, 2, 5010944, 3899392, 473088, 211200, 2929664, 168448, 5003520, 210432, 5419264, 4438784, 6564588, 5847039, 9217700, 6390735, 9651398, 9186534, 10329498, 12692890, 6973160, 6255868, 9692064, 6799564, 10125251, 9595363, 10803862, 13101462, 2324, 0, 1936896, 22528, 3282944, 0, 4813568, 3701248, 17937, 14080, 2345728, 37888, 3822848, 13056, 5287680, 4109824, 74086, 10866, 2468400, 34650, 3029310, 6753, 3576594, 4955922, 548450, 92014, 2877228, 246870, 3438139, 415581, 4050702, 5430030, 6095484, 6029946, 8946490, 6054504, 11609432, 6488701, 12287272, 10256428, 6569593, 6438263, 9420341, 6462819, 12018004, 6962553, 12761125, 10664744, 7609815, 6757335, 10198679, 6912449, 11156406, 9314005, 10456968, 13147267, 8018388, 7165907, 10607508, 7386300, 11565234, 9722834, 10931076, 13556096, 3, 1, 2389760, 22016, 3545088, 4, 5600512, 4619776, 144896, 13568, 2863872, 37120, 3953920, 13312, 6074624, 5028096, 6957295, 5780735, 9155002, 5606629, 9520333, 5712891, 10788254, 11975349, 7431660, 6255103, 9629367, 6015201, 9928649, 6121464, 11262619, 12384178, 4393550, 4131406, 6915591, 4088882, 8923690, 4065360, 9405440, 8359173, 4670279, 4408391, 7192576, 4431147, 9200419, 4342089, 9616384, 8636160, 10893311, 10175999, 13612274, 10719743, 14111231, 13580799, 14723815, 16759784, 11170303, 10452735, 13889004, 10996479, 14322431, 13857791, 15000801, 16774369, 3807076, 3868750, 6331175, 3761720, 7808299, 3867983, 9273344, 8095746, 4018269, 4145736, 6608160, 4103728, 8019748, 4144712, 9484544, 8372736, 4468402, 4011966, 6862717, 4101543, 7358348, 4335789, 8036448, 9284704, 4810924, 4288952, 7139703, 4378529, 7700613, 4612519, 8247897, 9561689, 10490059, 10358728, 13406599, 10383540, 16004005, 10883018, 16681846, 14716281, 10701251, 10635713, 13617793, 10660014, 16280734, 11094468, 16761967, 14927731, 11873023, 11151615, 14658788, 11306751, 15485439, 13708543, 14851285, 16755409, 12215551, 11363071, 14869982, 11518207, 15827709, 13985279, 15128271, 16769995, 4130896, 3868751, 6784265, 3892278, 7873836, 3868242, 9929472, 8948483, 4407882, 4276552, 7126530, 4103214, 8216358, 4144715, 10205952, 9225472, 11351807, 10175231, 13615359, 9935359, 13914623, 10107135, 15248365, 16370175, 11563007, 10451967, 13892095, 10212351, 14191359, 10318591, 15525093, 16646140};
    private static final int[] new_0 = new int[]{0, 57, 14912, 19066, 8, 67, 14680, 23215, 4979, 13002, 28625, 32767, 9369, 13524, 28395, 36863, 983063, 1048696, 274304, 347564, 1048636, 1310839, 1126040, 613853, 1577384, 1716735, 943359, 1082111, 1712848, 1979391, 1795071, 1348095, 3411968, 3092480, 2715648, 2590000, 3613952, 3814663, 3501846, 2921572, 4015418, 3696012, 3319183, 3193537, 4217438, 4418200, 4170919, 3590390, 2490368, 2105111, 1724207, 1667411, 2690816, 2760743, 2510153, 1802114, 3159128, 2774184, 2327745, 2270948, 3294339, 3429816, 3244764, 2470911, 5963776, 5119028, 5067854, 4811132, 6359594, 5970764, 5987930, 4880552, 6566278, 5722820, 5671391, 5414655, 6963131, 6574302, 6591467, 5549311, 4521994, 4590443, 3882867, 3888808, 5046337, 5311359, 4603019, 3960009, 5186203, 5259518, 4486399, 4557823, 5648594, 5980415, 5272063, 4694271, 6955776, 6832640, 6190592, 6130478, 7089664, 7028736, 6979331, 6464595, 7559205, 7501697, 6794120, 6799551, 7693139, 7697795, 7648404, 7067875, 10363392, 10435891, 9663285, 10323845, 10497536, 11156295, 10383692, 10396318, 11032415, 11170502, 10266822, 10927359, 11101067, 11759833, 11052765, 11065343, 4001536, 3747339, 3369491, 3308364, 4136704, 4337431, 4221227, 3705475, 4276033, 4153242, 3709856, 3714267, 4477290, 4677796, 4561594, 4111103, 7015680, 7089227, 6316114, 6389120, 7085328, 7351882, 7167596, 6655663, 7355768, 7495385, 6721761, 6795007, 7425694, 7692504, 7507961, 7061247, 9453824, 9134080, 8691712, 8566019, 9589760, 9856256, 9543424, 8897592, 9793802, 9474397, 9097567, 8971666, 9995823, 10196584, 9949303, 9303492, 8531712, 8146688, 7700227, 7708711, 8732416, 8802560, 8617247, 7843924, 8871975, 8552570, 8106129, 8049332, 9007185, 9142662, 8957610, 8249314, 12004352, 11160839, 11109410, 10852432, 12401152, 11946784, 11963949, 10922108, 12344662, 11500950, 11449775, 11192799, 12741261, 12352686, 12304313, 11327999, 10492928, 10500671, 9793351, 9864828, 10955285, 11287634, 10579039, 9870493, 10767977, 10906574, 10199252, 10205439, 11295905, 11627745, 10853866, 10276095, 12997376, 12808960, 12166656, 12171778, 13065728, 13070592, 13020672, 12440870, 13272064, 13214542, 12572760, 12577936, 13406241, 13476435, 13426788, 12844979, 16405248, 16477701, 15639304, 16365401, 16539136, 16739099, 16425502, 16372338, 16745517, 16752532, 15979668, 16705512, 16748377, 16751786, 16765611, 16777215};
    private static final int[] new_1 = new int[]{0, 0, 24832, 20992, 395776, 0, 162304, 33536, 14592, 15872, 48128, 44544, 222464, 22784, 54784, 56832, 1310834, 1048696, 2192427, 943429, 2956112, 3019106, 2787869, 4363541, 1133916, 940129, 2019092, 770350, 2782778, 2911307, 2614791, 4124928, 2162755, 2621489, 2715648, 1857033, 4395029, 2556459, 4553728, 4162816, 2049836, 2442267, 2542592, 1618688, 4221440, 2383382, 4380672, 3989504, 2359419, 2359675, 2785081, 1667411, 3941208, 2626924, 3575587, 4626715, 2184548, 2186854, 2612259, 1494076, 3767618, 2388566, 3468044, 4518661, 3473489, 3735626, 4416010, 3101991, 6359594, 3866692, 6322176, 5406464, 3357242, 3554867, 4243200, 2928912, 6186261, 3692589, 6149120, 5233152, 4128911, 3932298, 5009995, 3498597, 5904237, 5311359, 5080123, 6786607, 3950712, 3755124, 4836660, 3325518, 5796440, 5203816, 4907044, 6613275, 4587614, 4784215, 5334547, 4020270, 6492475, 4654162, 6979331, 6653953, 4407880, 4671040, 5096192, 3912727, 6384677, 4481083, 6871552, 6545920, 8323313, 7800055, 8683697, 7041227, 9642703, 7740140, 9670552, 10396318, 8148442, 7692512, 8445083, 6868149, 9469626, 7567062, 9497731, 10222985, 4718684, 4915286, 5466644, 4348718, 7148601, 4785745, 6915079, 6589701, 4276033, 4473659, 5095936, 3846932, 6777632, 4415031, 6478592, 6087680, 8061166, 7865843, 8945062, 7696065, 9708748, 9837277, 9671577, 11247248, 7688916, 7495385, 8508812, 7259814, 9403313, 9466563, 9170047, 10745463, 8978622, 9437357, 9468282, 8609926, 11213201, 9440167, 11437401, 10915415, 8605092, 9062802, 9097567, 8239210, 10841975, 9003917, 10935615, 10544446, 9241335, 9178103, 9603253, 8485583, 10759380, 9379560, 10393758, 11444886, 8739805, 8807389, 9232538, 8049332, 10388153, 9008845, 10023043, 10942846, 10289357, 10485957, 11234438, 9920162, 13243302, 10618303, 13140594, 12224879, 9846705, 10109867, 10798187, 9549447, 12741261, 10247845, 12638808, 11722839, 10944767, 10748159, 11762631, 10316768, 12722665, 12129531, 11832759, 13604779, 10505969, 10375659, 11326380, 9814982, 12351439, 11627745, 11462300, 13233810, 11337946, 11600083, 12087182, 10838442, 13310647, 11406798, 13797503, 13472124, 11028415, 11226041, 11716468, 10467983, 12939676, 11036083, 13426788, 13101155, 15205119, 14683647, 15567359, 13859327, 16460799, 14558207, 16423423, 16755967, 14703359, 14247423, 15065599, 13357567, 16024319, 14187519, 16052474, 16777215};
    Mode2Bpp(BufferedImage image, PALETTE selectedPalette, boolean dither, boolean resize) {
        this.selectedPalette = selectedPalette;
        this.palette = switch (selectedPalette) {
            case OLD_0 -> new Palette(old_0);
            case OLD_1 -> new Palette(old_1);
            case OLD_2 -> new Palette(Mode.mergeAtIndexC(Arrays.copyOf(old_0, 512), old_1, 256));
            case NEW_0 -> new Palette(new_0);
            case NEW_1 -> new Palette(new_1);
            case NEW_2 -> new Palette(Mode.mergeAtIndexC(Arrays.copyOf(new_0, 512), new_1, 256));
                // TODO add 512 color modes
        };
        if (resize)
            image = Mode.resizeImage(image, 80, 100);
        this.image = new PaletteImage(image, this.palette, dither);
        this.width = this.image.getWidth();
        this.height = this.image.getHeight();
    }

    private int[] getRow(int rowNum) {
        int[] row = new int[this.width * 2];
        for (int section = 0; section < this.width * 2; section+=2) {
            int pixelIndex = image.getPixelIndex(section/2, rowNum);
            if (selectedPalette == PALETTE.NEW_0 || selectedPalette == PALETTE.OLD_0)
                row[section] = 0x55;
            else if (selectedPalette == PALETTE.NEW_1 || selectedPalette == PALETTE.OLD_1)
                row[section] = 0x13;
            else if (selectedPalette == PALETTE.NEW_2 || selectedPalette == PALETTE.OLD_2) {
                if (pixelIndex > 255) row[section] = 0x13;
                else row[section] = 0x55;
                pixelIndex = pixelIndex % 256;
            }
            row[section+1] = pixelIndex;
        }
        return row;
    }

    public int[] get() {
        int[] im = new int[this.width * this.height * 2];
        for (int row = 0; row < this.height; row++) {
            Mode.mergeAtIndex(im, getRow(row), row * this.width * 2);
        }
        return im;
    }

    @Override
    public BufferedImage getImage() {
        return Mode.scaleImage(image.toImage(), 2, 1);
    }

    @Override
    public int[] getCom() {
        String comName = "2B.com";
        try (InputStream in = getClass().getResourceAsStream("/com/"+comName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            int length = in.available();
            int[] outputData = new int[16000 + length];
            for (int i = 0; i < length; i++) {
                outputData[i] = reader.read();
            }
            Mode.mergeAtIndex(outputData, this.get(), length);
            return outputData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}