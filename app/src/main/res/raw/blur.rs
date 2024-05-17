#pragma version(1)
#pragma rs java_package_name(com.example.foodrecipesv3)

rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;
uint32_t width;
uint32_t height;
float radius;

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
    float4 sum = 0;
    float count = 0;

    for (int i = -radius; i <= radius; i++) {
        for (int j = -radius; j <= radius; j++) {
            int xi = x + i;
            int yi = y + j;

            if (xi >= 0 && xi < width && yi >= 0 && yi < height) {
                uchar4 pixel = rsGetElementAt_uchar4(gIn, xi, yi);
                sum += convert_float4(pixel);
                count++;
            }
        }
    }

    sum /= count;
    sum.a = 1.0f;
    *v_out = convert_uchar4(sum);
}

void filter() {
    rsForEach(gScript, gIn, gOut);
}
